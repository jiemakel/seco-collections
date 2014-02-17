/**
 * 
 */
package fi.seco.collections.collection.primitive;

import com.carrotsearch.hppc.LongFloatOpenHashMap;

/**
 * @author jiemakel
 * 
 */
public class LongFloatOpenHashMapWithRemoveIndex extends LongFloatOpenHashMap {

	public void removeIndex(int slot) {
		assigned--;
		shiftConflictingKeys(slot);
	}

}
