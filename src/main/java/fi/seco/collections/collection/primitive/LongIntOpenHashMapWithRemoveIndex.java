/**
 * 
 */
package fi.seco.collections.collection.primitive;

import com.carrotsearch.hppc.LongIntOpenHashMap;

/**
 * @author jiemakel
 * 
 */
public class LongIntOpenHashMapWithRemoveIndex extends LongIntOpenHashMap {

	public void removeIndex(int slot) {
		assigned--;
		shiftConflictingKeys(slot);
	}

}
