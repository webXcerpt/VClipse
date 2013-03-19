/*******************************************************************************
 * Copyright (c) 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.vclipse.vcml.compare.experimental;

import static com.google.common.base.Predicates.not;
import static org.eclipse.emf.compare.utils.ReferenceUtil.getAsList;
import static org.eclipse.emf.compare.utils.ReferenceUtil.safeEGet;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.utils.DiffUtil;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The diff engine is in charge of actually computing the differences between the objects mapped by a
 * {@link Match} object.
 * <p>
 * This default implementation aims at being generic enough to be used for any model, whatever the metamodel.
 * However, specific differences, refinements of differences or even higher level differences might be
 * necessary.
 * </p>
 * TODO document available extension possibilities.
 * 
 * @author <a href="mailto:laurent.goubet@obeo.fr">Laurent Goubet</a>
 */
public class DefaultDiffEngine implements IDiffEngine {
	/**
	 * We'll use this "placeholder" to differentiate the unmatched elements from the "null" values that
	 * attributes can legitimately use.
	 */
	protected static final Object UNMATCHED_VALUE = new Object();

	/**
	 * The diff processor that will be used by this engine. Should be passed by the constructor and accessed
	 * by {@link #getDiffProcessor()}.
	 */
	private IDiffProcessor diffProcessor;

	/**
	 * Create the diff engine.
	 * 
	 * @param processor
	 *            this instance will be called for each detected difference.
	 */
	public DefaultDiffEngine(IDiffProcessor processor) {
		this.diffProcessor = processor;
	}

	/**
	 * This predicate can be used to check whether a given element is contained within the given iterable
	 * according to the semantics of {@link IEqualityHelper#matchingValues(Object, Object)} before returning
	 * it.
	 * 
	 * @param comparison
	 *            This will be used in order to retrieve the Match for EObjects when comparing them.
	 * @param iterable
	 *            Iterable which content we are to check.
	 * @param <E>
	 *            Type of the reference iterable's content.
	 * @return The useable predicate.
	 */
	protected <E> Predicate<E> containedIn(final Comparison comparison, final Iterable<E> iterable) {
		return new Predicate<E>() {
			public boolean apply(E input) {
				return contains(comparison, iterable, input);
			}
		};
	}

	/**
	 * Checks whether the given {@code iterable} contains the given {@code element} according to the semantics
	 * of {@link IEqualityHelper#matchingValues(Comparison, Object, Object)}.
	 * 
	 * @param comparison
	 *            This will be used in order to retrieve the Match for EObjects when comparing them.
	 * @param iterable
	 *            Iterable which content we are to check.
	 * @param element
	 *            The element we expect to be contained in {@code  iterable}.
	 * @param <E>
	 *            Type of the input iterable's content.
	 * @return {@code true} if the given {@code iterable} contains {@code element}, {@code false} otherwise.
	 */
	protected <E> boolean contains(Comparison comparison, Iterable<E> iterable, E element) {
		for (E candidate : iterable) {
			if (comparison.getEqualityHelper().matchingValues(candidate, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.diff.IDiffEngine#diff(org.eclipse.emf.compare.Comparison,
	 *      org.eclipse.emf.common.util.Monitor)
	 */
	public void diff(Comparison comparison, Monitor monitor) {
		for (Match rootMatch : comparison.getMatches()) {
			checkForDifferences(rootMatch, monitor);
		}
	}

	/**
	 * Checks the given {@link Match}'s sides for potential differences. Will recursively check for
	 * differences on submatches.
	 * 
	 * @param match
	 *            The match that is to be checked.
	 * @param monitor
	 *            The monitor to report progress or to check for cancellation.
	 */
	protected void checkForDifferences(Match match, Monitor monitor) {
		checkResourceAttachment(match, monitor);

		final FeatureFilter featureFilter = createFeatureFilter();

		final Iterator<EReference> references = featureFilter.getReferencesToCheck(match);
		while (references.hasNext()) {
			final EReference reference = references.next();
			final boolean considerOrdering = featureFilter.checkForOrderingChanges(reference);
			computeDifferences(match, reference, considerOrdering);
		}

		final Iterator<EAttribute> attributes = featureFilter.getAttributesToCheck(match);
		while (attributes.hasNext()) {
			final EAttribute attribute = attributes.next();
			final boolean considerOrdering = featureFilter.checkForOrderingChanges(attribute);
			computeDifferences(match, attribute, considerOrdering);
		}

		for (Match submatch : match.getSubmatches()) {
			checkForDifferences(submatch, monitor);
		}
	}

	/**
	 * Checks whether the given {@link Match}'s sides have changed resources. This will only be called for
	 * {@link Match} elements referencing the root(s) of an EMF Resource.
	 * 
	 * @param match
	 *            The match that is to be checked.
	 * @param monitor
	 *            The monitor to report progress or to check for cancellation.
	 */
	protected void checkResourceAttachment(Match match, Monitor monitor) {
		final Comparison comparison = match.getComparison();

		if (comparison.getMatchedResources().isEmpty()) {
			// This is a comparison of EObjects, do not go up to the resources
			return;
		}

		final boolean originIsRoot = isRoot(match.getOrigin());

		if (comparison.isThreeWay()) {
			if (originIsRoot) {
				// Uncontrol or delete, the "resource attachment" is a deletion
				if (!isRoot(match.getLeft())) {
					final String uri = match.getOrigin().eResource().getURI().toString();
					getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE,
							DifferenceSource.LEFT);
				}
				if (!isRoot(match.getRight())) {
					final String uri = match.getOrigin().eResource().getURI().toString();
					getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE,
							DifferenceSource.RIGHT);
				}
			} else {
				// Control or add, the "resource attachment" is an addition
				if (isRoot(match.getLeft())) {
					final String uri = match.getLeft().eResource().getURI().toString();
					getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD,
							DifferenceSource.LEFT);
				}
				if (isRoot(match.getRight())) {
					final String uri = match.getRight().eResource().getURI().toString();
					getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD,
							DifferenceSource.RIGHT);
				}
			}
		} else {
			final boolean leftIsRoot = isRoot(match.getLeft());
			final boolean rightIsRoot = isRoot(match.getRight());
			if (leftIsRoot && !rightIsRoot) {
				final String uri = match.getLeft().eResource().getURI().toString();
				getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD,
						DifferenceSource.LEFT);
			} else if (!leftIsRoot && rightIsRoot) {
				final String uri = match.getRight().eResource().getURI().toString();
				getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE,
						DifferenceSource.LEFT);
			}
		}
	}

	/**
	 * Checks whether the given EObject is a root of its resource or not.
	 * 
	 * @param eObj
	 *            The EObject to check.
	 * @return <code>true</code> if this object is a root of its containing resource, <code>false</code>
	 *         otherwise.
	 */
	protected static boolean isRoot(EObject eObj) {
		if (eObj instanceof InternalEObject) {
			return ((InternalEObject)eObj).eDirectResource() != null;
		}

		boolean isRoot = false;
		if (eObj != null) {
			final Resource res = eObj.eResource();
			final EObject container = eObj.eContainer();
			// <root of containment tree> || <root of fragment>
			isRoot = (container == null && res != null)
					|| (container != null && container.eResource() != res);
		}
		return isRoot;
	}

	/**
	 * Computes the difference between the sides of the given {@code match} for the given containment
	 * {@code reference}.
	 * <p>
	 * This is only meant for three-way comparisons.
	 * </p>
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param reference
	 *            The containment reference which values are to be checked.
	 * @param checkOrdering
	 *            {@code true} if we should consider ordering changes on this reference, {@code false}
	 *            otherwise.
	 */
	protected void computeContainmentDifferencesThreeWay(Match match, EReference reference,
			boolean checkOrdering) {
		final Comparison comparison = match.getComparison();

		// We won't use iterables here since we need random access collections for fast LCS.
		final List<Object> leftValues = getAsList(match.getLeft(), reference);
		final List<Object> rightValues = getAsList(match.getRight(), reference);
		final List<Object> originValues = getAsList(match.getOrigin(), reference);

		final List<Object> lcsOriginLeft = DiffUtil.longestCommonSubsequence(comparison, originValues,
				leftValues);
		final List<Object> lcsOriginRight = DiffUtil.longestCommonSubsequence(comparison, originValues,
				rightValues);

		// TODO Can we shortcut in any way?

		// Which values have "changed" in any way?
		final Iterable<Object> changedLeft = Iterables.filter(leftValues, not(containedIn(comparison,
				lcsOriginLeft)));
		final Iterable<Object> changedRight = Iterables.filter(rightValues, not(containedIn(comparison,
				lcsOriginRight)));

		// Added or moved in left
		for (Object diffCandidate : changedLeft) {
			/*
			 * This value is not in the LCS between origin and left, we thus know it has changed. We also know
			 * that this is a containment reference.
			 */
			final Match candidateMatch = comparison.getMatch((EObject)diffCandidate);

			if (contains(comparison, originValues, diffCandidate)) {
				// This object is contained in both the left and origin lists. It can only have moved
				if (checkOrdering) {
					featureChange(match, reference, diffCandidate, DifferenceKind.MOVE, DifferenceSource.LEFT);
				}
			} else {
				// This object is not contained in the origin list.
				// If single-valued, this is either a CHANGE or a MOVE (moved from another container)
				// If multi-valued references, ADD or MOVE
				if (candidateMatch != null && candidateMatch.getOrigin() != null) {
					featureChange(match, reference, diffCandidate, DifferenceKind.MOVE, DifferenceSource.LEFT);
				} else {
					featureChange(match, reference, diffCandidate, DifferenceKind.ADD, DifferenceSource.LEFT);
				}
			}
		}

		// Added or moved in right
		for (Object diffCandidate : changedRight) {
			/*
			 * This value is not in the LCS between origin and right, we thus know it has changed. We also
			 * know that this is a containment reference.
			 */
			final Match candidateMatch = comparison.getMatch((EObject)diffCandidate);

			if (contains(comparison, originValues, diffCandidate)) {
				// This object is contained in both the right and origin lists. It can only have moved
				if (checkOrdering) {
					featureChange(match, reference, diffCandidate, DifferenceKind.MOVE,
							DifferenceSource.RIGHT);
				}
			} else {
				// This object is not contained in the origin list.
				// If single-valued, this is either a CHANGE or a MOVE (moved from another container)
				// If multi-valued references, ADD or MOVE
				if (candidateMatch != null && candidateMatch.getOrigin() != null) {
					featureChange(match, reference, diffCandidate, DifferenceKind.MOVE,
							DifferenceSource.RIGHT);
				} else {
					featureChange(match, reference, diffCandidate, DifferenceKind.ADD, DifferenceSource.RIGHT);
				}
			}
		}

		// deleted from either side
		for (Object diffCandidate : originValues) {
			/*
			 * A value that is in the origin but not in the left/right either has been deleted or is a moved
			 * element which previously was in this reference. We'll detect the move on its new reference.
			 */
			final Match candidateMatch = comparison.getMatch((EObject)diffCandidate);
			if (candidateMatch == null) {
				// out of scope
			} else {
				if (candidateMatch.getLeft() == null) {
					featureChange(match, reference, diffCandidate, DifferenceKind.DELETE,
							DifferenceSource.LEFT);
				}
				if (candidateMatch.getRight() == null) {
					featureChange(match, reference, diffCandidate, DifferenceKind.DELETE,
							DifferenceSource.RIGHT);
				}
			}
		}
	}

	/**
	 * Computes the difference between the sides of the given {@code match} for the given containment
	 * {@code reference}.
	 * <p>
	 * This is only meant for two-way comparisons.
	 * </p>
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param reference
	 *            The containment reference which values are to be checked.
	 * @param checkOrdering
	 *            {@code true} if we should consider ordering changes on this reference, {@code false}
	 *            otherwise.
	 */
	protected void computeContainmentDifferencesTwoWay(Match match, EReference reference,
			boolean checkOrdering) {
		final Comparison comparison = match.getComparison();

		final List<Object> leftValues = getAsList(match.getLeft(), reference);
		final List<Object> rightValues = getAsList(match.getRight(), reference);

		final List<Object> lcs = DiffUtil.longestCommonSubsequence(comparison, rightValues, leftValues);

		// TODO Can we shortcut in any way?

		// Which values have "changed" in any way?
		final Iterable<Object> changed = Iterables.filter(leftValues, not(containedIn(comparison, lcs)));

		// Added or moved
		for (Object diffCandidate : changed) {
			/*
			 * This value is not in the LCS between right and left, we thus know it has changed. We also know
			 * that this is a containment reference.
			 */
			final Match candidateMatch = comparison.getMatch((EObject)diffCandidate);

			if (contains(comparison, rightValues, diffCandidate)) {
				// This object is contained in both the left and right lists. It can only have moved
				if (checkOrdering) {
					featureChange(match, reference, diffCandidate, DifferenceKind.MOVE, DifferenceSource.LEFT);
				}
			} else {
				// This object is not contained in the right list.
				// If single-valued, this is either a CHANGE or a MOVE (moved from another container)
				// If multi-valued references, ADD or MOVE
				if (candidateMatch != null && candidateMatch.getRight() != null) {
					featureChange(match, reference, diffCandidate, DifferenceKind.MOVE, DifferenceSource.LEFT);
				} else {
					featureChange(match, reference, diffCandidate, DifferenceKind.ADD, DifferenceSource.LEFT);
				}
			}
		}

		// deleted
		for (Object diffCandidate : rightValues) {
			/*
			 * A value that is in the right but not in the left either has been deleted or is a moved element
			 * which previously was in this reference. We'll detect the move on its new reference.
			 */
			try {
			final Match candidateMatch = comparison.getMatch((EObject)diffCandidate);
			if (candidateMatch == null) {
				// out of scope
			} else if (candidateMatch.getLeft() == null) {
				featureChange(match, reference, diffCandidate, DifferenceKind.DELETE, DifferenceSource.LEFT);
			}
			} catch (ClassCastException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Computes the difference between the sides of the given <code>match</code> for the given
	 * <code>attribute</code>.
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param attribute
	 *            The attribute which values are to be checked.
	 * @param checkOrdering
	 *            <code>true</code> if we should consider ordering changes on this attribute,
	 *            <code>false</code> otherwise.
	 */
	protected void computeDifferences(Match match, EAttribute attribute, boolean checkOrdering) {
		final Comparison comparison = match.getComparison();

		// This default implementation does not care about "attribute" changes on added/removed elements
		boolean shortcut = false;
		if (comparison.isThreeWay()) {
			shortcut = match.getOrigin() == null;
		} else {
			shortcut = match.getLeft() == null || match.getRight() == null;
		}
		if (shortcut) {
			return;
		}

		if (attribute.isMany()) {
			if (comparison.isThreeWay()) {
				computeMultiValuedFeatureDifferencesThreeWay(match, attribute, checkOrdering);
			} else {
				computeMultiValuedFeatureDifferencesTwoWay(match, attribute, checkOrdering);
			}
		} else {
			computeSingleValuedAttributeDifferences(match, attribute);
		}
	}

	/**
	 * Computes the difference between the sides of the given <code>match</code> for the given
	 * <code>reference</code>.
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param reference
	 *            The reference which values are to be checked.
	 * @param checkOrdering
	 *            <code>true</code> if we should consider ordering changes on this reference,
	 *            <code>false</code> otherwise.
	 */
	protected void computeDifferences(Match match, EReference reference, boolean checkOrdering) {
		final Comparison comparison = match.getComparison();

		if (reference.isContainment()) {
			if (comparison.isThreeWay()) {
				computeContainmentDifferencesThreeWay(match, reference, checkOrdering);
			} else {
				computeContainmentDifferencesTwoWay(match, reference, checkOrdering);
			}
		} else if (reference.isMany()) {
			if (comparison.isThreeWay()) {
				computeMultiValuedFeatureDifferencesThreeWay(match, reference, checkOrdering);
			} else {
				computeMultiValuedFeatureDifferencesTwoWay(match, reference, checkOrdering);
			}
		} else {
			if (comparison.isThreeWay()) {
				computeSingleValuedReferenceDifferencesThreeWay(match, reference);
			} else {
				computeSingleValuedReferenceDifferencesTwoWay(match, reference);
			}
		}
	}

	/**
	 * Computes the difference between the sides of the given {@code match} for the given multi-valued
	 * {@code feature}.
	 * <p>
	 * The given {@code feature} cannot be a containment reference.
	 * </p>
	 * <p>
	 * This is only meant for three-way comparisons.
	 * </p>
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param feature
	 *            The feature which values are to be checked.
	 * @param checkOrdering
	 *            {@code true} if we should consider ordering changes on this feature, {@code false}
	 *            otherwise.
	 */
	protected void computeMultiValuedFeatureDifferencesThreeWay(Match match, EStructuralFeature feature,
			boolean checkOrdering) {
		final Comparison comparison = match.getComparison();

		// We won't use iterables here since we need random access collections for fast LCS.
		final List<Object> leftValues = getAsList(match.getLeft(), feature);
		final List<Object> rightValues = getAsList(match.getRight(), feature);
		final List<Object> originValues = getAsList(match.getOrigin(), feature);

		final List<Object> lcsOriginLeft = DiffUtil.longestCommonSubsequence(comparison, originValues,
				leftValues);
		final List<Object> lcsOriginRight = DiffUtil.longestCommonSubsequence(comparison, originValues,
				rightValues);

		// Which values have "changed" in any way?
		final Iterable<Object> changedLeft = Iterables.filter(leftValues, not(containedIn(comparison,
				lcsOriginLeft)));
		final Iterable<Object> changedRight = Iterables.filter(rightValues, not(containedIn(comparison,
				lcsOriginRight)));

		// Added or moved in the left
		for (Object diffCandidate : changedLeft) {
			/*
			 * This value is not in the LCS between origin and left, we thus know it has changed. If it is
			 * present in the origin, this is a move. Otherwise, it has been added in the left list.
			 */
			if (contains(comparison, originValues, diffCandidate)) {
				if (checkOrdering) {
					featureChange(match, feature, diffCandidate, DifferenceKind.MOVE, DifferenceSource.LEFT);
				}
			} else {
				featureChange(match, feature, diffCandidate, DifferenceKind.ADD, DifferenceSource.LEFT);
			}
		}

		// Added or moved in the right
		for (Object diffCandidate : changedRight) {
			/*
			 * This value is not in the LCS between origin and right, we thus know it has changed. If it is
			 * present in the origin, this is a move. Otherwise, it has been added in the right list.
			 */
			if (contains(comparison, originValues, diffCandidate)) {
				if (checkOrdering) {
					featureChange(match, feature, diffCandidate, DifferenceKind.MOVE, DifferenceSource.RIGHT);
				}
			} else {
				featureChange(match, feature, diffCandidate, DifferenceKind.ADD, DifferenceSource.RIGHT);
			}
		}

		// Removed from either side
		for (Object diffCandidate : originValues) {
			// A value that is in the origin but not in one of the side has been deleted.
			// However, we do not want attribute changes on removed elements.
			if (!contains(comparison, leftValues, diffCandidate)) {
				if (feature instanceof EReference || match.getLeft() != null) {
					featureChange(match, feature, diffCandidate, DifferenceKind.DELETE, DifferenceSource.LEFT);
				}
			}
			if (!contains(comparison, rightValues, diffCandidate)) {
				if (feature instanceof EReference || match.getRight() != null) {
					featureChange(match, feature, diffCandidate, DifferenceKind.DELETE,
							DifferenceSource.RIGHT);
				}
			}
		}
	}

	/**
	 * Computes the difference between the sides of the given {@code match} for the given multi-valued
	 * {@code feature}.
	 * <p>
	 * The given {@code feature} cannot be a containment reference.
	 * </p>
	 * <p>
	 * This is only meant for two-way comparisons.
	 * </p>
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param feature
	 *            The feature which values are to be checked.
	 * @param checkOrdering
	 *            {@code true} if we should consider ordering changes on this feature, {@code false}
	 *            otherwise.
	 */
	protected void computeMultiValuedFeatureDifferencesTwoWay(Match match, EStructuralFeature feature,
			boolean checkOrdering) {
		final Comparison comparison = match.getComparison();

		// We won't use iterables here since we need random access collections for fast LCS.
		final List<Object> leftValues = getAsList(match.getLeft(), feature);
		final List<Object> rightValues = getAsList(match.getRight(), feature);

		final List<Object> lcs = DiffUtil.longestCommonSubsequence(comparison, rightValues, leftValues);

		// Which values have "changed" in any way?
		final Iterable<Object> changed = Iterables.filter(leftValues, not(containedIn(comparison, lcs)));

		// Added or moved
		for (Object diffCandidate : changed) {
			/*
			 * This value is not in the LCS between right and left, we thus know it has changed. If it is
			 * present in the right, this is a move. Otherwise, it has been added in the left list.
			 */
			if (contains(comparison, rightValues, diffCandidate)) {
				if (checkOrdering) {
					featureChange(match, feature, diffCandidate, DifferenceKind.MOVE, DifferenceSource.LEFT);
				}
			} else {
				featureChange(match, feature, diffCandidate, DifferenceKind.ADD, DifferenceSource.LEFT);
			}
		}

		// deleted
		for (Object diffCandidate : rightValues) {
			// A value that is in the right but not in the left has been deleted.
			// However, we do not want attribute changes on removed elements.
			if (!contains(comparison, leftValues, diffCandidate)) {
				if (feature instanceof EReference || match.getLeft() != null) {
					featureChange(match, feature, diffCandidate, DifferenceKind.DELETE, DifferenceSource.LEFT);
				}
			}
		}
	}

	/**
	 * Computes the difference between the sides of the given <code>match</code> for the given single-valued
	 * <code>attribute</code>.
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param attribute
	 *            The attribute which values are to be checked.
	 */
	protected void computeSingleValuedAttributeDifferences(Match match, EAttribute attribute) {
		final Comparison comparison = match.getComparison();

		Object leftValue = UNMATCHED_VALUE;
		if (match.getLeft() != null) {
			leftValue = safeEGet(match.getLeft(), attribute);
		}
		Object rightValue = UNMATCHED_VALUE;
		if (match.getRight() != null) {
			rightValue = safeEGet(match.getRight(), attribute);
		}

		IEqualityHelper helper = comparison.getEqualityHelper();
		if (helper.matchingValues(leftValue, rightValue)) {
			// Identical values in left and right. The only problematic case is if they do not match the
			// origin (and left and right are defined, i.e don't detect attribute change on unmatched)
			if (leftValue != UNMATCHED_VALUE && comparison.isThreeWay()) {
				final Object originValue;
				if (match.getOrigin() == null) {
					originValue = null;
				} else {
					originValue = safeEGet(match.getOrigin(), attribute);
				}
				final boolean matchingLO = helper.matchingValues(leftValue, originValue);

				/*
				 * if !matchingLO, the same change has been made on both side. This is actually a
				 * pseudo-conflict. It can be either a set or unset diff according to the value of origin.
				 */
				if (!matchingLO && isNullOrEmptyString(originValue)) {
					// The same value has been SET on both sides
					getDiffProcessor().attributeChange(match, attribute, leftValue, DifferenceKind.CHANGE,
							DifferenceSource.LEFT);
					getDiffProcessor().attributeChange(match, attribute, rightValue, DifferenceKind.CHANGE,
							DifferenceSource.RIGHT);
				} else if (!matchingLO) {
					// The same value has been UNSET from both sides
					getDiffProcessor().attributeChange(match, attribute, originValue, DifferenceKind.CHANGE,
							DifferenceSource.LEFT);
					getDiffProcessor().attributeChange(match, attribute, originValue, DifferenceKind.CHANGE,
							DifferenceSource.RIGHT);
				}
			}
		} else if (match.getOrigin() != null) {
			final Object originValue = safeEGet(match.getOrigin(), attribute);

			if (helper.matchingValues(leftValue, originValue)) {
				Object changedValue = rightValue;
				if (isNullOrEmptyString(rightValue)) {
					changedValue = originValue;
				}

				if (rightValue != UNMATCHED_VALUE) {
					// Value is in left and origin, but not in the right
					getDiffProcessor().attributeChange(match, attribute, changedValue, DifferenceKind.CHANGE,
							DifferenceSource.RIGHT);
				} else {
					// Right is unmatched, left is the same as in the origin. No diff here : the diff is on
					// the match itself, not on one of its attributes.
				}
			} else if (helper.matchingValues(rightValue, originValue)) {
				Object changedValue = leftValue;
				if (isNullOrEmptyString(leftValue)) {
					changedValue = originValue;
				}

				if (leftValue != UNMATCHED_VALUE) {
					// Value is in right and origin, but not in left
					getDiffProcessor().attributeChange(match, attribute, changedValue, DifferenceKind.CHANGE,
							DifferenceSource.LEFT);
				} else {
					// Left is unmatched, right is the same as in the origin. No diff here : the diff is on
					// the match itself, not on one of its attributes.
				}
			} else {
				/*
				 * Left and right are different. None match what's in the origin. Those of the two that are
				 * not unmatched are thus a "change" difference, with a possible conflict.
				 */
				Object leftChange = leftValue;
				if (isNullOrEmptyString(leftValue)) {
					leftChange = originValue;
				}
				Object rightChange = rightValue;
				if (isNullOrEmptyString(rightValue)) {
					rightChange = originValue;
				}

				if (leftValue != UNMATCHED_VALUE) {
					getDiffProcessor().attributeChange(match, attribute, leftChange, DifferenceKind.CHANGE,
							DifferenceSource.LEFT);
				}
				if (rightValue != UNMATCHED_VALUE) {
					getDiffProcessor().attributeChange(match, attribute, rightChange, DifferenceKind.CHANGE,
							DifferenceSource.RIGHT);
				}
			}
		} else {
			// Left and right values are different, and we have no origin.
			if (leftValue != UNMATCHED_VALUE) {
				if (isNullOrEmptyString(leftValue)) {
					getDiffProcessor().attributeChange(match, attribute, rightValue, DifferenceKind.CHANGE,
							DifferenceSource.LEFT);
				} else {
					getDiffProcessor().attributeChange(match, attribute, leftValue, DifferenceKind.CHANGE,
							DifferenceSource.LEFT);
				}
			}
			if (comparison.isThreeWay() && rightValue != UNMATCHED_VALUE) {
				if (isNullOrEmptyString(rightValue)) {
					getDiffProcessor().attributeChange(match, attribute, leftValue, DifferenceKind.CHANGE,
							DifferenceSource.RIGHT);
				} else {
					getDiffProcessor().attributeChange(match, attribute, rightValue, DifferenceKind.CHANGE,
							DifferenceSource.RIGHT);
				}
			}
		}
	}

	/**
	 * Returns {@code true} if the given {@code object} is {@code null} or the empty String.
	 * 
	 * @param object
	 *            The object we need to test.
	 * @return {@code true} if the given {@code object} is {@code null} or the empty String.
	 */
	private boolean isNullOrEmptyString(Object object) {
		return object == null || object instanceof String && ((String)object).length() == 0;
	}

	/**
	 * Returns {@code true} if the given {@code object} is {@code null} or the {@link #UNMATCHED_VALUE}.
	 * 
	 * @param object
	 *            The object we need to test.
	 * @return {@code true} if the given {@code object} is {@code null} or the {@link #UNMATCHED_VALUE}.
	 */
	private boolean isNullOrUnmatched(Object object) {
		return object == null || object == UNMATCHED_VALUE;
	}

	/**
	 * Computes the difference between the sides of the given <code>match</code> for the given single-valued
	 * <code>reference</code>.
	 * <p>
	 * The given {@code reference} cannot be a containment reference.
	 * </p>
	 * <p>
	 * This is only meant for three-way comparisons.
	 * </p>
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param reference
	 *            The reference which values are to be checked.
	 */
	protected void computeSingleValuedReferenceDifferencesThreeWay(Match match, EReference reference) {
		final Comparison comparison = match.getComparison();

		Object leftValue = UNMATCHED_VALUE;
		if (match.getLeft() != null) {
			leftValue = safeEGet(match.getLeft(), reference);
		}
		Object rightValue = UNMATCHED_VALUE;
		if (match.getRight() != null) {
			rightValue = safeEGet(match.getRight(), reference);
		}
		Object originValue = UNMATCHED_VALUE;
		if (match.getOrigin() != null) {
			originValue = safeEGet(match.getOrigin(), reference);
		}

		boolean distinctValueLO = !comparison.getEqualityHelper().matchingValues(leftValue, originValue);
		// consider null and unmatched as the same
		distinctValueLO = distinctValueLO
				&& !(isNullOrUnmatched(leftValue) && isNullOrUnmatched(originValue));

		if (distinctValueLO) {
			// Left and origin are distinct
			if (leftValue == null || leftValue == UNMATCHED_VALUE) {
				// Left has been removed
				getDiffProcessor().referenceChange(match, reference, (EObject)originValue,
						DifferenceKind.CHANGE, DifferenceSource.LEFT);
			} else {
				// Left has been set to a new value, or left has been added altogether
				getDiffProcessor().referenceChange(match, reference, (EObject)leftValue,
						DifferenceKind.CHANGE, DifferenceSource.LEFT);
			}
		}

		boolean distinctValueRO = !comparison.getEqualityHelper().matchingValues(rightValue, originValue);
		// consider null and unmatched as the same
		distinctValueRO = distinctValueRO
				&& !(isNullOrUnmatched(rightValue) && isNullOrUnmatched(originValue));

		if (distinctValueRO) {
			// Right and origin are distinct
			if (rightValue == null || rightValue == UNMATCHED_VALUE) {
				// right value is unset, or right has been removed
				getDiffProcessor().referenceChange(match, reference, (EObject)originValue,
						DifferenceKind.CHANGE, DifferenceSource.RIGHT);
			} else {
				// Right has been set to a new value, or right has been added altogether
				getDiffProcessor().referenceChange(match, reference, (EObject)rightValue,
						DifferenceKind.CHANGE, DifferenceSource.RIGHT);
			}
		}
	}

	/**
	 * Computes the difference between the sides of the given <code>match</code> for the given single-valued
	 * <code>reference</code>.
	 * <p>
	 * The given {@code reference} cannot be a containment reference.
	 * </p>
	 * <p>
	 * This is only meant for two-way comparisons.
	 * </p>
	 * 
	 * @param match
	 *            The match which sides we need to check for potential differences.
	 * @param reference
	 *            The reference which values are to be checked.
	 */
	protected void computeSingleValuedReferenceDifferencesTwoWay(Match match, EReference reference) {
		final Comparison comparison = match.getComparison();

		Object leftValue = UNMATCHED_VALUE;
		if (match.getLeft() != null) {
			leftValue = safeEGet(match.getLeft(), reference);
		}
		Object rightValue = UNMATCHED_VALUE;
		if (match.getRight() != null) {
			rightValue = safeEGet(match.getRight(), reference);
		}

		boolean distinctValue = !comparison.getEqualityHelper().matchingValues(leftValue, rightValue);
		// consider null and unmatched as the same
		distinctValue = distinctValue && !(isNullOrUnmatched(leftValue) && isNullOrUnmatched(rightValue));

		if (distinctValue) {
			if (leftValue == null || leftValue == UNMATCHED_VALUE) {
				// left value is unset, or left has been removed
				getDiffProcessor().referenceChange(match, reference, (EObject)rightValue,
						DifferenceKind.CHANGE, DifferenceSource.LEFT);
			} else {
				// Left has been set to a new value, or left has been added altogether
				getDiffProcessor().referenceChange(match, reference, (EObject)leftValue,
						DifferenceKind.CHANGE, DifferenceSource.LEFT);
			}
		}
	}

	/**
	 * This will be used in order to create the {@link FeatureFilter} that should be used by this engine to
	 * determine the structural features on which it is to try and detect differences.
	 * 
	 * @return The newly created feature filter.
	 */
	protected FeatureFilter createFeatureFilter() {
		return new FeatureFilter();
	}

	/**
	 * Delegates to the diff processor to create the specified feature change.
	 * 
	 * @param match
	 *            The match on which values we detected a diff.
	 * @param feature
	 *            The exact feature on which a diff was detected.
	 * @param value
	 *            The value for which we detected a changed.
	 * @param kind
	 *            The kind of difference to create.
	 * @param source
	 *            The source from which originates that diff.
	 */
	protected void featureChange(Match match, EStructuralFeature feature, Object value, DifferenceKind kind,
			DifferenceSource source) {
		if (feature instanceof EAttribute) {
			getDiffProcessor().attributeChange(match, (EAttribute)feature, value, kind, source);
		} else if (value instanceof EObject) {
			getDiffProcessor().referenceChange(match, (EReference)feature, (EObject)value, kind, source);
		}
	}

	/**
	 * This will return the diff processor that has been created through {@link #createDiffProcessor()} for
	 * this differencing process.
	 * 
	 * @return The diff processor to notify of difference detections.
	 */
	protected final IDiffProcessor getDiffProcessor() {
		return diffProcessor;
	}
}
