MatrixProduct {

	/**
	 * Determine whether a matrix can be multiplied by another object.
	 */
	*areCompatible {
		arg m, other;
		if (other.isKindOf(Matrix)) {
			^ (m.columnSize() === other.rowSize());
		};
		if (other.isKindOf(Vector)) {
			^ (m.columnSize() === other.size());
		};
		^ other.isKindOf(Number);
	}

	*notCompatible {
		arg m, other;
		^ this.areCompatible(m, other).not();
	}

	*matrixMatrixProduct {
		arg m, other;
		var result;

		if (this.notCompatible(m, other)) {
			Exception("% * % product requires equal column (%) and row (%) sizes.".format(m.class, other.class, m.columnSize.cs, other.rowSize.cs)).throw();
		};

		^ other.collect {
			arg otherVector;
			this.matrixVectorProduct(m, otherVector);
		}.asMatrix();
	}

	*matrixVectorProduct {
		arg m, other;

		if (this.notCompatible(m, other)) {
			Exception("% * % product requires equal column (%) and vector (%) sizes.".format(m.class, other.class, m.columnSize, other.size)).throw();
		};

		^ other.collect {
			arg scalar, index;
			(m[index] * scalar);
		}.sum;
	}
}
