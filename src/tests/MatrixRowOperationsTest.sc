MatrixRowOperationsTest : MatrixTest {
	test_rowOperations {
		m = Matrix[[0, 1, 4], [2, 4, 5]];
		m.swapRow(0, 2);
		this.assertEquals(m.row(0), Vector[4, 5], "First row is swapped.");
		this.assertEquals(m.row(2), Vector[0, 2], "Second row is swapped.");
		this.assertEquals(m.row(1), Vector[1, 4], "Middle row is unchanged.");

		m.scaleRow(0, -2);
		this.assertEquals(m.row(0), Vector[-8, -10], "Rows are scaled.");

		m.addRow(0, 1, -0.5);
		this.assertEquals(m.row(1), Vector[5, 9], "Row 1 is scaled and added to row 2.");
		this.assertEquals(m.row(0), Vector[-8, -10], "Row 1 is unchanged.");
	}

	test_reverseRows {
		m = Matrix[[0, 1, 4], [2, 4, 5]];
		m = m.reverseRows();
		this.assertEquals(m.row(0), Vector[4, 5], "Rows are in reverse order.");
		this.assertEquals(m.row(1), Vector[1, 4], "Rows are in reverse order.");
		this.assertEquals(m.row(2), Vector[0, 2], "Rows are in reverse order.");
	}



}
