package fr.xgouchet.webmonitor.test;

import java.util.List;

import android.test.AndroidTestCase;
import fr.xgouchet.webmonitor.diff.Diff;
import fr.xgouchet.webmonitor.diff.DiffOperation;
import fr.xgouchet.webmonitor.diff.DiffParser;

public class DiffTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEquals() {
		String test = "Hello world, my name is Xavier";

		DiffParser parser = new DiffParser(0);
		parser.setIgnoreWS(false);
		List<Diff> diffs = parser.diff(test, new String(test));

		assertNotNull(diffs);
		assertTrue(diffs.size() == 1);

		Diff diff = diffs.get(0);
		assertEquals(diff.getText(), test);
		assertEquals(diff.getOperation(), DiffOperation.EQUAL);
	}

	public void testEqualsWS() {
		String test = "   Hello world, my name is Xavier";
		String test2 = "Hello    world, \t my  \r\n name is \n Xavier\n";

		DiffParser parser = new DiffParser(0);
		parser.setIgnoreWS(true);
		List<Diff> diffs = parser.diff(test, test2);

		assertNotNull(diffs);
		assertTrue(diffs.size() == 1);

		Diff diff = diffs.get(0);
		assertEquals(diff.getText(), test2);
		assertEquals(diff.getOperation(), DiffOperation.EQUAL);
	}

	public void testAddAfter() {
		String test = "Hello world, my name is Xavier";
		String test2 = "Hello world, my name is Xavier, and I'm an Android developer!";

		List<Diff> diffs = new DiffParser(0).diff(test, new String(test));

		assertNotNull(diffs);
		assertTrue(diffs.size() == 2);

		Diff diff = diffs.get(0);
		assertEquals(diff.getText(), test);
		assertEquals(diff.getOperation(), DiffOperation.EQUAL);

		diff = diffs.get(1);
		assertEquals(diff.getText(), ", and I'm an Android developer!");
		assertEquals(diff.getOperation(), DiffOperation.INSERT);
	}

	public void testAddBefore() {
		String test = "I'm an Android developer!";
		String test2 = "Hello world, my name is Xavier, and I'm an Android developer!";

		List<Diff> diffs = new DiffParser(0).diff(test, test2);

		assertNotNull(diffs);
		assertTrue(diffs.size() == 2);

		Diff diff = diffs.get(0);
		assertEquals(diff.getText(), "Hello world, my name is Xavier, and ");
		assertEquals(diff.getOperation(), DiffOperation.INSERT);

		diff = diffs.get(1);
		assertEquals(diff.getText(), test);
		assertEquals(diff.getOperation(), DiffOperation.EQUAL);
	}
}
