package com.etheller.collections;

public interface Collection<T> extends CollectionView<T> {
	/**
	 * @param what
	 *            The thing to add
	 * @return true if collection was changed
	 */
	boolean add(T what);

	boolean remove(T what);

	void clear();

	final class Util {
		public static <TYPE> void addAll(final Collection<TYPE> list, final CollectionView<TYPE> toAdd) {
			for (final TYPE item : toAdd) {
				list.add(item);
			}
		}

		public static <TYPE> void removeAll(final Collection<TYPE> list, final CollectionView<TYPE> toRemove) {
			for (final TYPE item : toRemove) {
				list.remove(item);
			}
		}

		private Util() {
		}

		public static <T> boolean isEmpty(final List<T> collection) {
			return collection.size() == 0;
		}
	}
}
