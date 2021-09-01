VectorTest : VectorAbstractTest {
	var v;

	setUp {
		v = Vector.newClear(3);
	}

	tearDown {
		v.free;
	}

	test_create {
		this.assertEquals(v.size, 3, "Max size is correctly set.");
		this.assertFloatEquals(v[0], 0, "0 element is correctly empty");
		this.assertFloatEquals(v[1], 0, "1 element is correctly empty");
		this.assertFloatEquals(v[2], 0, "2 element is correctly empty");
	}

	test_createFilled {
		v = Vector[1, 0, -4, -2.3];
		this.assertEquals(v.size, 4, "Max size is correctly set.");
		this.assertFloatEquals(v[0], 1, "first element is correctly set");
		this.assertFloatEquals(v[1], 0, "0 element is correctly set");
		this.assertFloatEquals(v[2], -4, "neg element is correctly set");
		this.assertFloatEquals(v[3], -2.3, "float element is correctly set");

	}

	test_equals {
		var v2 = Vector[1, 2, 3];
		v = Vector[1, 2, 3];
		this.assertEquals(v2, v, "Integer vectors are equal.");
		v[2] = 2.999999;
		this.assertEquals(v2, v, "Integer vectors are still equal.");
	}

	test_dot {
		var v2 = Vector[1, 2, 3, 4];
		v = Vector[4, 3, 2, 1];
		this.assertFloatEquals(v.dot(v2), 20, "Dot product is calculated.");
		this.assertFloatEquals(v.selfDot(), 30, "Self-dot product is calculated.")
	}

	test_l1 {
		v = Vector[-1, 2, -3, 4];
		this.assertFloatEquals(v.l1(), 10, "L1 is correctly calculated.");
	}

	test_l2 {
		v = Vector[-1, 2, -3, 4];
		this.assertFloatEquals(v.l2(), 5.4772, "L2 is correctly calculated.");
	}

	test_add {
		v = Vector[1, 2, 3];
		// v = v + 2;
		v = 2 + v;
		this.assert(v.isKindOf(Vector), "Adding maintains vectorness");
	}

	test_transposition {
		v = Vector[1, 2, 3];
		this.assertEquals(v.transposition, Matrix[[1], [2], [3]], "Vector transposition returns a 1 - 2 - 3 Matrix.");
		this.assert(v.transposition.isKindOf(Matrix), "Vector transposition returns a type Matrix.");
	}

	test_normalize {
		var v2 = Vector[-4, 3, 2];
		v = Vector[3, 4, 0];
		this.assertEquals(v.normalize(\l1), Vector[0.4285714328289, 0.57142859697342, 0], "l1 normalization works.");
		this.assertEquals(v.normalize, Vector[0.6, 0.8, 0], "l2 normalization works.");
		this.assertEquals(v.normalize(\maxNorm), Vector[0.75, 1.0, 0], "max normalization works.");
		this.assertFloatEquals(v.normalize.l2, 1, "The norm of a normalized is 1.");
		this.assertFloatEquals(v.normalize.dot(v2.normalize), 0, "The dot product of two orthogonal normalized vectors is 0.");
		this.assertFloatEquals(v.normalize.selfDot(), 1, "The self dot product of a normalized vectors is 1.");
		this.assertFloatEquals(v2.normalize.selfDot(), 1, "The self dot product of a normalized vectors is 1.");
	}


}
