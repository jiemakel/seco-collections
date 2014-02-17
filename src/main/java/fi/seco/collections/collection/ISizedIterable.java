package fi.seco.collections.collection;

public interface ISizedIterable<T> extends Iterable<T> {
	long size();
}
