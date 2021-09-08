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

	test_transpose {
		v = Vector[1, 2, 3];
		this.assertEquals(v.transpose, Matrix[[1], [2], [3]], "Vector transpose returns a 1 - 2 - 3 Matrix.");
		this.assert(v.transpose.isKindOf(Matrix), "Vector transpose returns a type Matrix.");
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

	test_mean {
		v = Vector[1, 4, -3, 6];
		this.assertFloatEquals(v.mean, 2, "Mean of a vector is corresctly calculated.");
		this.assertFloatEquals(v.standardDeviation.standardDeviation, 3.39116, "Standard deviation of a vector is corresctly calculated.");
		this.assertFloatEquals(v.standardDeviation.confidence, 1.6955824957813, "Standard deviation confidence of a vector is corresctly calculated.");
	}

	test_orthogonalTo {
		var v1 = Vector[2, 3, 4];
		var v2 = Vector[-3, 2, 0];

		this.assert(v1.orthogonalTo(v2), "V1 is orthogonal to orthogonal v2.");
		this.assert(v1.parallelTo(v2).not, "V1 is not parallel to orthogonal v2.");

		v2 = Vector[-3, -4.5, -6];
		this.assert(v1.orthogonalTo(v2).not, "V1 is not orthogonal to parallel v2.");
		this.assert(v1.parallelTo(v2), "V1 is parallel to parallel v2.");

		v2 = Vector[-3, -1.5, 4];
		this.assert(v1.orthogonalTo(v2).not, "V1 is not orthogonal to random v2.");
		this.assert(v1.parallelTo(v2).not, "V1 is parallel to random v2.");
	}

	test_outer {
		var a = Vector[1, 2, 3, 4];
		var b = Vector[5, 6, -1];
		this.assertEquals(a.outer(b), Matrix[Vector[ 5.0, 10.0, 15.0, 20.0 ], Vector[ 6.0, 12.0, 18.0, 24.0 ], Vector[ -1.0, -2.0, -3.0, -4.0 ]], "Outer Product is calculated.");
	}


}
