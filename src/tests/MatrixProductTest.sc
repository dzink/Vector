MatrixProductTest : MatrixTest {

	test_compatible {
		var m = Matrix[[3, 4, 5, 6], [7, 8, 9, 10]];
		this.assert(m.isCompatible(Matrix.columns([2, 1], [2, 1])), "4x2 is compatible with 2x2.");
		this.assert(m.isCompatible(Matrix.columns([2, 1], [2, 1], [2, 1])), "4x2 is compatible with 2x3.");
		this.assert(m.notCompatible(Matrix.columns([2, 1, 3], [2, 1, 9])), "4x2 is not compatible with 3x2.");

		this.assert(m.isCompatible(Vector[2, 3]), "4x2 is compatible R2 vector.");
		this.assert(m.notCompatible(Vector[2, 3, 5, 7]), "4x2 is not compatible with a R4 vector.");
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
}
