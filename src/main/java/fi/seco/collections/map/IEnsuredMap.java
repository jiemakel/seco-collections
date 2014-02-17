package fi.seco.collections.map;

import java.util.Map;

public interface IEnsuredMap<T1, T2> extends Map<T1, T2> {
	public T2 ensure(T1 key);
}
