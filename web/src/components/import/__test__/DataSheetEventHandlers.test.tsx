import '@testing-library/jest-dom';
import {describe, test, expect} from '@jest/globals';
import { createNewPosSlot } from "../DataSheetEventHandlers";

describe("Datasheet Event Handlers", () => {
  test("Verify create new position slot", () => {
    // Sample to mimic the structure of event in the grid where
    // we only concern the pos value for this testing
    const e = {
      context: {
        rowData: [
            { pos: 10 },
            { pos: 15 }
        ]
      }
    };
    // No need to shift position as we have empty slot
    let m = createNewPosSlot(e, 11);
    expect(m.get(10)).toEqual(10)
    expect(m.get(15)).toEqual(15)

    // Need to shift as position crashes, so pos 10 -> 11 and 15 -> 16
    m = createNewPosSlot(e, 10);
    expect(m.get(10)).toEqual(11)
    expect(m.get(15)).toEqual(16)
  });
});
