package org.vclipse.configscan.utils;

import java.util.List;

public abstract class AbstractTreeTraverser<T> {

	protected boolean atLeastOneHit;
	
	public T getNextItem(T item) {
		atLeastOneHit = propertyHit(item);
		return getNextItem(item, getParent(item) == null ? -1 : getChildren(getParent(item)).indexOf(item));
	}
	
	protected T getNextItem(T item, int index) {
		if(item != null) {
			List<T> children = getChildren(item);
			if(!children.isEmpty()) {
				for(int i=0, last=children.size() - 1; i<children.size(); i++) {
					T childItem = children.get(i);
					if(i <= index) {
						continue;
					} else if(!getChildren(childItem).isEmpty()) {
						return getNextItem(childItem, index);
					} else if(i == last) {
						T parentItem = getParent(item);
						List<T> parentsChildren = getChildren(parentItem);
						int newIndex = parentsChildren.indexOf(item);
						if(newIndex == parentsChildren.size() - 1) {
							newIndex = -1;
							if(!atLeastOneHit || getParent(parentItem) == null) {
								return null;
							}
						}
						return getNextItem(parentItem, newIndex);
					} else if(propertyHit(childItem)) {
						return childItem;
					}
				}
			} else {
				return getNextItem(getParent(item), index);
			}
		}
		return null;
	}
	
	public T getPreviousItem(T item) {
		atLeastOneHit = propertyHit(item);
		return getPreviousItem(getParent(item), getParent(item) == null ? -1 : getChildren(getParent(item)).indexOf(item));
	}
	
	protected T getPreviousItem(T item, int index) {
		if(item != null) {
			List<T> children = getChildren(item);
			if(!children.isEmpty()) {
				for(int first=0, i=index; i>=first; i--) {
					T child = children.get(i);
					if(!getChildren(child).isEmpty()) {
						return getPreviousItem(child, getChildren(child).size() - 1);
					} else if(i >= index) {
						continue;
					} else if(propertyHit(child)) {
						return child;
					} else if(i == first) {
						T parentItem = getParent(item);
						int newIndex = getChildren(parentItem).indexOf(item) - 1;
						if(newIndex == -1) {
							newIndex = getChildren(parentItem).size() - 1;
							if(!atLeastOneHit || getParent(parentItem) == null) {
								return null;
							}
						}
						return getPreviousItem(parentItem, newIndex);
					} 
				}
			} else {
				return getPreviousItem(getParent(item), index);
			}
		}
		return null;
	}
	
	protected abstract T getParent(T item);
	
	protected abstract List<T> getChildren(T item);
	
	protected abstract boolean propertyHit(T item);
}
