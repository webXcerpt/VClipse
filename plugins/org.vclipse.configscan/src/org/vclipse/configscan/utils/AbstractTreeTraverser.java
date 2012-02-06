package org.vclipse.configscan.utils;

import java.util.List;

abstract class AbstractTreeTraverser<T> {

	protected static final int BAD_INDEX = -1;
	
	public T getNextItem(T item) {
		if(!getChildren(item).isEmpty()) {
			return getNextItem(item, 0);
		}
		
		T parent = getParent(item);
		if(parent != null) {
			List<T> children = getChildren(parent);
			int indexOf = children.indexOf(item);
			if(++indexOf <= children.size()) {
				return getNextItem(parent, children.indexOf(item) + 1);
			}
			return getNextItem(parent, children.indexOf(item));
		}
		return null;
	}
	
	protected T getNextItem(T parent, int index) {
		if(parent == null || index == BAD_INDEX) {
			return null;
		}
		List<T> children = getChildren(parent);
		if(children.isEmpty()) {
			T parentOfParent = getParent(parent);
			return getNextItem(parentOfParent, getChildren(parentOfParent).indexOf(parent) + 1);
		}
		if(index >= children.size()) {
			T parentOfParent = getParent(parent);
			if(parentOfParent == null) {
				return getNextItem(parent, 0);
			}
			List<T> parentsChildren = getChildren(parentOfParent);
			int indexOf = parentsChildren.indexOf(parent);
			if((indexOf + 1) >= parentsChildren.size()) {
				getNextItem(parentOfParent, indexOf);
			}
			return getNextItem(parentOfParent, indexOf + 1);
		}
		T item = children.get(index);
		if(propertyTest(item)) {
			return item;
		}
		if(!getChildren(item).isEmpty()) {
			return getNextItem(item, 0);
		}
		return getNextItem(parent, ++index);
	}
	
	public T getPreviousItem(T item) {
		T parent = getParent(item);
		if(parent != null) {
			int indexOf = getChildren(parent).indexOf(item);
			if(--indexOf > BAD_INDEX) {
				return getPreviousItem(parent, indexOf);
			}
			T parentOfParent = getParent(parent);
			if(parentOfParent == null) {
				return getPreviousItem(item, getChildren(item).size() - 1);
			}
			return getPreviousItem(getParent(parent), 
					getChildren(getParent(parent)).indexOf(parent) - 1);
		}
		if(!getChildren(item).isEmpty()) {
			return getPreviousItem(item, 0);
		}
		return null;
	}
	
	public T getPreviousItem(T parent, int index) {
		if(parent == null) {
			return null;
		}
		if(index <= BAD_INDEX) {
			T parentOfParent = getParent(parent);
			if(parentOfParent == null) {
				return getPreviousItem(parent, getChildren(parent).size() - 1);
			}
			return getPreviousItem(parentOfParent, getChildren(parentOfParent).indexOf(parent) - 1);
		}
		List<T> children = getChildren(parent);
		if(children.isEmpty()) {
			T parentOfParent = getParent(parent);
			return getPreviousItem(parentOfParent, getChildren(parentOfParent).indexOf(parent) - 1);
		}
		
		T item = children.get(index);
		if(propertyTest(item)) {
			return item;
		}
		if(!getChildren(item).isEmpty()) {
			return getPreviousItem(item, getChildren(item).size() - 1);
		}
		return getPreviousItem(parent, --index);
	}

	protected abstract T getParent(T item);
	
	protected abstract List<T> getChildren(T item);
	
	protected abstract boolean propertyTest(T item);
}
