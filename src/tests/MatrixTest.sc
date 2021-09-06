MatrixTest : VectorAbstractTest {
	var m;

	setUp {
		m = Matrix.newClear(4, 4);
	}

	tearDown {
		m.free;
	}

	test_new {
		m = Matrix.new();
		m = Matrix.newClear(2, 3);
		this.assertEquals(m.columnSize, 2, "Clear Matrix is the right column size.");
		this.assertEquals(m.rowSize, 3, "Clear Matrix is the right rows size.");
	}

	test_set {
		var v = Vector[3, 4, 5];
		m = Matrix[[1, 2, 3], v, nil];
		this.assertEquals(m.columnSize, 3, "The new matrix is the correct size");
		this.assert(m[0].isKindOf(Vector), "Vectors are added");
		this.assertEquals(m[1].identityHash, v.identityHash, "Actual objects are added when they are vectors");
	}

	test_rowsColumns {
		m = Matrix.rows(Vector[1, 2, 3], [4, 5, 6], [7, 8, 9]);
		this.assertEquals(m, Matrix[Vector[ 1.0, 4.0, 7.0 ], Vector[ 2.0, 5.0, 8.0 ], Vector[ 3.0, 6.0, 9.0 ]], "Creating matrix by rows works.");

		m = Matrix.columns([1, 4, 7], [2, 5, 8], [3, 6, 9]);
		this.assertEquals(m, Matrix[Vector[ 1.0, 4.0, 7.0 ], Vector[ 2.0, 5.0, 8.0 ], Vector[ 3.0, 6.0, 9.0 ]], "Creating matrix by columns works.");
	}

	test_sort {
		m = Matrix[[0, 2, 3, 5], [1, 4, 2, 6], [-1, 5, 9, -2]];

		this.assertEquals(m.sortColumnsBy(0)[0], Vector[-1, 5, 9, -2], "Sort by first element.");
		this.assertEquals(m.sortColumnsBy(1)[1], Vector[1, 4, 2, 6], "Sort by second element.");

		m = Matrix[[0, 3, 6, 9], [1, -5, 7, -10], [-1, 2, 5, 4]];
		this.assertEquals(m.sortRowsBy(2)[2], Vector[-1, 2, 4, 5], "Sort rows by third element.");
		this.assertEquals(m.sortRowsBy(2)[1], Vector[1, -5, -10, 7], "Second column is sorted by the third column's order.");
	}

	test_identity {
		m = Matrix.identity(4);
		this.assertEquals(m, Matrix[[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]], "Identity is set.");

		m = Matrix.rows([1, 2, 4], [5, 6, 8]);
		this.assertEquals(m * m.rightIdentity(), Matrix.rows([1, 2, 4], [5, 6, 8]), "Right identity retains original matrix with long matrix.");
		this.assertEquals(m.leftIdentity() * m, Matrix.rows([1, 2, 4], [5, 6, 8]), "Left identity retains original matrix with long matrix.");

		m = Matrix.columns([1, 2, 4], [5, 6, 8]);
		this.assertEquals(m.leftIdentity * m, Matrix.columns([1, 2, 4], [5, 6, 8]), "Identity retains original matrix with tall matrix.");
		this.assertEquals(m * m.rightIdentity, Matrix.columns([1, 2, 4], [5, 6, 8]), "Identity retains original matrix with tall matrix.");
	}

	test_scalar {
		m = Matrix[[1, 2], [3, 4]];
		m = m + 2;
		this.assert(m.isKindOf(Matrix), "m stays a matrix after adding");
		this.assertEquals(m, Matrix[[3, 4], [5, 6]], "Matrix can be added to a scalar");

		m = m * 3;
		this.assertEquals(m, Matrix[[9, 12], [15, 18]], "Matrix can be multiplied by a scalar");
	}

	test_scalarMatrix {
		var m2 = Matrix[[0, 2], [1, 4]];
		m = Matrix[[3, 1], [-3, 2]];
		m = m + m2;
		this.assertEquals(m , Matrix[[3, 3], [-2, 6]], "Matrices are added.");
	}

	test_transpose {
		m = Matrix[[3, 1], [-3, 2]];
		this.assert(m.transpose.isKindOf(Matrix), "transpose maintains matrix.");
		this.assertEquals(m.transpose(), Matrix[[3, -3], [1, 2]], "transpose is successful.");

		m = Matrix[[1, 2, 3], [4, 5, 6]];
		this.assertEquals(m.transpose(), Matrix[[1, 4], [2, 5], [3, 6]], "Rectangular matrices are transposed also.");
	}

	test_vectorRow {
		m = Matrix[[0, 1, 4], [2, 4, 5]];

		this.assertEquals(m.rowSize(), 3, "Matrices accurately count rowSize.");
		this.assertEquals(m.row(0), Vector[0, 2], "Matrices can get rows.");
		this.assertEquals(m.row(2), Vector[4, 5], "Matrices can get rows.");
	}

	test_chop {
		m = Matrix[[0, 1, 4], [2, 4, 5], [1, 2, 1], [3, 2, 6], [5, -1, 2]];
		this.assertEquals(m.chop(0, 2), Matrix[[0, 1, 4], [2, 4, 5], [1, 2, 1]], "Chop removes end vectors.");
		this.assertEquals(m.chop(3, inf), Matrix[[3, 2, 6], [5, -1, 2]], "Chop removes start vectors.");
		this.assertEquals(m.chop(1, 3), Matrix[[2, 4, 5], [1, 2, 1], [3, 2, 6]], "Chop removes start and end vectors.");
		this.assertEquals(m.size, 5, "Chop doesn't affect the original matrix.");
	}

	test_compatible {
		var m = Matrix[[3, 4, 5, 6], [7, 8, 9, 10]];
		this.assert(m.compatibleWith(Matrix.columns([2, 1], [2, 1])), "4x2 is compatible with 2x2.");
		this.assert(m.compatibleWith(Matrix.columns([2, 1], [2, 1], [2, 1])), "4x2 is compatible with 2x3.");
		this.assert(m.notCompatibleWith(Matrix.columns([2, 1, 3], [2, 1, 9])), "4x2 is not compatible with 3x2.");

		this.assert(m.compatibleWith(Vector[2, 3]), "4x2 is compatible R2 vector.");
		this.assert(m.notCompatibleWith(Vector[2, 3, 5, 7]), "4x2 is not compatible with a R4 vector.");
	}

	test_vectorProduct {
		var v = Vector[1, 2];
		m = Matrix[[0, 1], [2, 4]];
		this.assertEquals(m.vectorProduct(v), Vector[4, 9], "Vector product is accurately calculated.");
		this.assertEquals(m * v, Vector[4, 9], "Vector product is accurately calculated with shorthand.");

		this.assertException({m * Vector[1, 2, 3]}, Exception, "Accurately throws an exception when attempting to dot multiply incompatible vector.");
	}

	test_matrixProduct {
		var m2 = Matrix.rows([1, 0, 1, 1], [2, 0, 1, -1], [3, 1, 0, 2]);
		m = Matrix.rows([1, -1, 2], [0, -2, 1]);
		this.assertEquals(m.product(m2), Matrix[ Vector[ 5.0, -1.0 ], Vector[ 2.0, 1.0 ], Vector[ 0.0, -2.0 ], Vector[ 6.0, 4.0 ] ], "Matrix product is accurately calculated.");
		this.assertEquals(m * m2, Matrix[ Vector[ 5.0, -1.0 ], Vector[ 2.0, 1.0 ], Vector[ 0.0, -2.0 ], Vector[ 6.0, 4.0 ] ], "Matrix product is accurately calculated with shorthand.");
		this.assertException({m * Matrix.rows([0, 2, 3, 5], [1, -1, 2, 7])}, Exception, "Accurately throws an exception when attempting to dot multiply incompatible matrix.");
	}

	test_hadamard {
		var m2 = Matrix[[1, 2], [3, 1]];
		m = Matrix[[0, 1], [2, 4.1]];
		this.assertEquals(m.hadamard(m2), Matrix[[0, 2], [6, 4.1]], "Hadamard product is accurately calculated.");
	}

	test_echelon {
		m = Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2]];
		this.assertEquals(m.lowerRowEchelon(), Matrix[Vector[ 4.5, 2.5, 1.0 ], Vector[ 0.0, 5.0, 6.0 ], Vector[ 0.0, 0.0, 2.0 ]], "Lower row echelon format is successful.");
		this.assertEquals(m.upperRowEchelon(), Matrix[Vector[ 1.0, 0.0, 0.0 ], Vector[ 3.0, -4.0, 0.0 ], Vector[ 5.0, -11.0, -11.25 ]], "Upper row echelon format is successful.");
		this.assertEquals(m.diagonal(), Matrix[Vector[ 1.0, 0.0, 0.0 ], Vector[ 0.0, -4.0, 0.0 ], Vector[ 0.0, 0.0, -11.25 ] ], "Diagonal format is successful.");
	}

	test_solve {
		var v = Vector[-1, 1, 2];
		var solution;
		m = Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2]];

		this.assertEquals(m.augment(v), Matrix[[1, 2, 1], [3, 2, 6], [5, -1, 2], [-1, 1, 2]], "Matrix is augmented properly");
		this.assertEquals(m.augment(v).rowEchelon, Matrix[[1, 0, 0], [3, -4, 0], [5, -11, -11.25], [-1, 3, 5.25]], "Matrix is converted to row echelon format properly.");
		this.assertEquals(m.augment(v).reducedRowEchelon, Matrix[Vector[1.0, -0.0, -0.0], Vector[3.0, 1.0, -0.0], Vector[5.0, 2.75, 1.0], Vector[-1.0, -0.75, -0.46666666865349]], "Matrix is reduced row echelon format properly.");

		solution = m.solve(v);
		(m.transpose).do {
			arg vector, i;
			this.assertFloatEquals(vector.dot(solution), v[i]);
		};

		this.assertException({m.solve*()}, Exception, "Throws an error when no solution vector is present.");

		// @TODO weird solutions around zero vector
		// solution = m.solve(Vector[0, 0, 0]).postln;
		// (m.transpose).do {
		// 	arg vector, i;
		// 	this.assertFloatEquals(vector.dot(solution), v[i]);
		// };
	}

	test_norm {
		m = Matrix[[1, 2, 3, -1],
			[2, -1, -4, 8],
			[-1, 1, 3, -5],
			[-1, 2, 5, -6],
			[-1, -2, -3, 1]];

		this.assertFloatEquals(m.l1(), 15, "L1 norm is accurately calculated.");
		this.assertFloatEquals((m * 5).l1, 75, "L1 norm is correctly scaled");
		this.assertFloatEquals(m.infNorm(), 21, "InfNorm is accurately calculated.");
		this.assertFloatEquals((m * 5).infNorm, 105, "InfNorm is correctly scaled");
		this.assertFloatEquals(m.froNorm(), 14.730919862656, "FroNorm is accurately calculated.");
		this.assertFloatEquals((m * 5).froNorm, 73.654599313281, "FroNorm is correctly scaled");
	}

	test_inverse {
		m = Matrix[[1, 2, 1],
              [-4, 4, 5],
              [6, 7, 7]];
		this.assertEquals((m.inverse * m).class, Matrix, "Inverse returns a Matrix");
		this.assertEquals((m.inverse * m), Matrix.identity(3), "Inverse creates a proper inverse");
	}

	test_decomposition {
		var l, u;
		m = Matrix[[2, 1, -6], [4, -4, -9], [-4, 3, 5]];
		#l, u = m.lu;

		this.assertEquals(l, Matrix[ Vector[ 1.0, 0.5, -3.0 ], Vector[ 0.0, 1.0, -0.5 ], Vector[ 0.0, 0.0, 1.0 ] ], "L is correctly decomposed.");
		this.assertEquals(u, Matrix[ Vector[ 2.0, 0.0, 0.0 ], Vector[ 4.0, -6.0, 0.0 ], Vector[ -4.0, 5.0, -4.5 ] ], "U is correctly decomposed.");
		this.assertEquals(l * u, m, "LU equals m.");
	}

	test_pivot {
		// @TODO
		var l, u;
		var i = Matrix.identity(3);
		m = Matrix.rows([0, 1, 0], [0, 0, -9], [-4, 0, 0]);
		// m.lu.postln;
		// m.postln;
		// m = m.addRow(2, 0);
		// m = m.addRow(0, 1);
		// m = m.addRow(1, 2);
		// m.swapRow(2, 0);
		// m.swapRow(2, 1);
		//
		// i.swapRow(2, 0);
		// i.swapRow(2, 1);
		//
		// m.print;
		// i.print;
		// (m * i.inverse).print;
		// m.lu.postln;
		m = Matrix.rows([1, 5, 0, 0],
              [2, 0, 0, 1],
              [0, 3, 9, 5],
              [6, 7, 9, 8]);
		// #l, u = m.lu;
		//
		// l.print;
		// u.print;
		// (l * u).print;
		# l, u, i = m.pivot;
		// l.print;
		// u.print;
		// i.print;
		// (l * u).print;
		// (m * i).print;
	}

	test_regression {
		var sums;
		m = Matrix[[140, 60, 22], [155, 62, 25], [159, 67, 24], [179, 70, 20], [192, 71, 15], [200, 72, 14], [212, 75, 14], [215, 78, 11]];
		// sums = m.sum;
		// sums.postln;
		// m.print;
		// m.regression(0, [1, 2]).postln;
	}

	test_determinant {
		m = Matrix[[1, 2, 3], [4, 5, 6], [7, 8, 9]];
		this.assertEquals(m.pr_dropColumnAndRow(1, 1), Matrix[[1, 3], [7, 9]], "Rows and Columns are properly dropped.");
		this.assertEquals(m, Matrix[[1, 2, 3], [4, 5, 6], [7, 8, 9]], "Original matrix is not affected when rows and columns are dropped.");

		m = Matrix[[6, 4, 2], [1, -2, 8], [1, 5, 7]];
		this.assertEquals(m.determinant, -306, "3x3 determinant is correctly calculated.");

		m = Matrix[[1, 2, 6, 3], [2, 3, 4, 5], [3, 4, 3, -2], [4, -5, 6, 1]];
		this.assertEquals(m.determinant, -776, "4x4 determinant is correctly calculated.");
	}

	test_eigenvalue {
		var m1 = Matrix[[3, 0], [1, 2]];
		var i = m1.identity(2);

		this.assert(m1.isEigenvalue(2), "Eigenvalue is confirmed.");
		this.assert(m1.isEigenvalue(3), "Other eigenvalue is confirmed.");
		this.assert(m1.isEigenvalue(4).not, "False eigenvalue is not confirmed.");

	}


}
