/**
 * Creates and operates on a matrix.
 */
Matrix[slot] : Array {

	*new {
		arg size;
		var m = super.new(size);
		^ m;
	}

	*newClear {
		arg size, vectorSize;
		var m = this.new(size);
		size.do {
			m.add(Vector.newClear(vectorSize));
		};
		^ m;
	}

	*scalar {
		arg size, scalar = 1;
		var m = this.newClear(size, size);
		size.do {
			arg i;
			m[i][i] = scalar;
		};
		^ m;
	}

	*identity {
		arg size;
		^ this.scalar(size, 1);
	}

	*columns {
		arg ... columns;
		var m = this.new();
		columns.do {
			arg column;
			m.grow(1);
			m.add(column);
		};
		^ m;
	}

	*rows {
		arg ... rows;
		var m = this.newClear(rows[0].size, rows.size);
		rows.do {
			arg row, i;
			m.putRow(i, row);
		};
		^ m;
	}

	// PUT operations

	/**
	 * Override to superclass to ensure that only vectors are added to the matrix.
	 */
	put {
		arg n, vector;
		super.put(n, vector.asVector);
		^ this;
	}

	insert {
		arg vector;
		super.insert(vector.asVector);
		^ this;
	}

	add {
		arg vector;
		super.add(vector.asVector);
		^ this;
	}

	// COLUMN OPERATIONS

	chop {
		arg first = 0, last = inf;
		if (last == inf) {
			last = this.columnSize;
		};
		^ this[first..last];
	}

	// ROW OPERATIONS

	/**
	 * Get the number of rows in the matrix.
	 */
	rowSize {
		this.do {
			arg vector;
			if (vector.notNil) {
				^ vector.size;
			};
		};
		^ 0;
	}

	dimensions {
		^ this.rowSize();
	}

	columnSize {
		^ this.size;
	}

	/**
	 * Return a given row as a vertex.
	 */
	row {
		arg index;
		^ this.collect {
			arg vector;
			vector[index];
		}.asVector();
	}

	/**
	 * Get an array of all rows.
	 */
	rows {
		var rows = Matrix(this.rowSize());
		(this.rowSize).do {
			arg rowIndex;
			rows.add(this.row(rowIndex));
		};
		^ rows;
	}

	/**
	 * Put a row into a given index in the matrix.
	 */
	putRow {
		arg n, vector;
		vector.do {
			arg element, index;
			this[index][n] = element;
		};
	}

	swapRow {
		arg rowId1, rowId2;
		^ MatrixRowOperations.swap(this, rowId1, rowId2);
	}

	scaleRow {
		arg rowId, scalar = 1;
		^ MatrixRowOperations.scale(this, rowId, scalar);
	}

	addRow {
		arg sourceRowId, targetRowId, scalar = 1;
		^ MatrixRowOperations.add(this, sourceRowId, targetRowId, scalar);
	}

	reverseRows {
		^ MatrixRowOperations.reverseOrder(this);
	}

	/**
	 * Sorting.
	 */

	 sortColumnsByLength {
		 arg algo = \l2;
		 ^ MatrixReshape.sortColumnsByLength(this, algo);
	 }

	 sortRowsByLength {
		 arg algo = \l2;
		 ^ MatrixReshape.sortRowsByLength(this, algo);
	 }

	 sortColumnsBy {
		 arg elementId = 0;
		 ^ MatrixReshape.sortColumnsBy(this, elementId);
	 }

	 sortRowsBy {
		 arg elementId = 0;
		 ^ MatrixReshape.sortRowsBy(this, elementId);
	 }

	transpose {
		^ MatrixReshape.transpose(this);
	}

	/**
	 * Place a vertex or matrix to the right of this matrix and return a new matrix.
	 */
	augment {
		arg other;
		var m = this.deepCopy();
		^ MatrixReshape.augment(m, other);
	}

	/**
	 * Convenience methods to create the right kinds of identities based on the current matrix.
	 */
	identity {
		arg scalar = 1;
		^ this.rightIdentity(scalar);
	}

	leftIdentity {
		arg scalar = 1;
		^ Matrix.scalar(this.rowSize, scalar);
	}

	rightIdentity {
		arg scalar = 1;
		^ Matrix.identity(this.columnSize, scalar);
	}

	rotation {
		arg ... radians;
		// @TODO
	}

	/**
	 * @TODO it would be nice to have a way to "check" a matrix but I'm not sure what that would look like at this time.
	 */
	checkVectors {

	}

	/**
	 * This is how SC will try to multiply arrays, sometimes you want this. Each cell is multiplied by the same cell in other, and a new matrix is returned.
	 */
	hadamard {
		arg other;
		^ MatrixProduct.hadamard(this, other);
	}

	vectorProduct {
		arg other;
		^ MatrixProduct.matrixVectorProduct(this, other);
	}

	isCompatible {
		arg other;
		^ MatrixProduct.areCompatible(this, other);
	}

	notCompatible {
		arg other;
		^ this.isCompatible(other).not();
	}

	matrixProduct {
		arg other;
		^ MatrixProduct.matrixMatrixProduct(this, other);
	}

	product {
		arg other;
		if (other.isKindOf(Vector)) {
			^ this.vectorProduct(other);
		};
		if (other.isKindOf(Matrix)) {
			^ this.matrixProduct(other);
		};
		^ super.perform('*', other);
	}

	// Solver

	inverse {
		^ MatrixSolver.inverse(this.deepCopy());
	}

	pr_gaussianFactor {
		arg sourceRow, targetRow, columnIndex;
		^ targetRow[columnIndex] / sourceRow[columnIndex]
	}

	lu {
		^ MatrixSolver.lu(this);
	}

	/**
	 * Perform a gaussian reduction on a matrix.
	 */
	upperRowEchelon {
		^ MatrixSolver.upperRowEchelon(this.deepCopy());
	}

	rowEchelon {
		^ this.upperRowEchelon();
	}

	lowerRowEchelon {
		^ MatrixSolver.lowerRowEchelon(this.deepCopy());
	}

	diagonal {
		^ MatrixSolver.diagonal(this.deepCopy());
	}

	/**
	 * Reduce each row at diagonal.
	 */
	reduceAtDiagonal {
		^ this.deepCopy().pr_reduceAtDiagonal();
	}

	pr_reduceAtDiagonal {
		this.rows.do {
			arg vector, i;
			this.scaleRow(i, vector[i].reciprocal);
		};
		^ this;
	}

	reducedRowEchelon {
		^ this.deepCopy().rowEchelon().pr_reduceAtDiagonal()
	}

	/**
	 * Solve a set of matrix equations using a gaussian reduction and then backsolving.
	 * Another option is m.diagonal.reduceAtDiagonal.last - this takes 50% longer, @TODO look into relative accuracy
	 */
	solve {
		arg solutionVector;
		var reduced;
		if (solutionVector.isNil) {
			Exception("% requires a vector to solve for.".format(this.class)).throw();
		};
		reduced = this.augment(solutionVector).reducedRowEchelon();

		// @TODO check for viable solutions
		^ reduced.pr_backsolve();
	}

	pr_backsolve{
		var solutions = Vector.fill(this.columnSize - 1, 0);

		// Address the reduced rowSize in reverse order like you learned in Linear algebra.
		((this.rowSize - 1)..0).do {
			arg rowIndex;
			var vector = this.row(rowIndex);
			var farSide, sum;
			farSide = vector.pop;
			sum = farSide - solutions.dot(vector);
			solutions[rowIndex] = sum;
		};
		^ solutions;
	}

	powerIteration {
		^ MatrixSolver.powerIteration(this);
	}

	gramSchmidt {
		^ MatrixSolver.gramSchmidt(this.deepCopy);
	}

	minors {

	}

	// NORM OPERATIONS

	/**
	 Frobenius norm.
	 */
	froNorm {
		var f = this.flatten;
		^ f.collect({
			arg n;
			n.squared;
		}).sum.sqrt;
	}

	infNorm {
		var max = 0;
		this.rows().do {
			arg vector;
			var sum = vector.abs().sum();
			max = max(sum, max);
		};
		^ max
	}

	l1 {
		var max = 0;
		this.do {
			arg vector;
			var sum = vector.abs().sum();
			max = max(sum, max);
		};
		^ max
	}

	determinant {
		if (this.size == 2) {
			^ (this[0][0] * this[1][1]) - (this[0][1] * this[1][0]);
		} {
			var total = 0;
			var evenOdd = 1;
			this.do {
				arg vector, i;
				var subDeterminant = this.pr_dropColumnAndRow(i, 0).determinant;
				subDeterminant = subDeterminant * vector[0] * evenOdd;
				total = total + subDeterminant;
				evenOdd = evenOdd * -1;
			};
			^ total;
		};

	}

	pr_dropColumnAndRow {
		arg columnId, rowId;
		var m = Matrix.new();
		if (columnId > 0) {
			m = m ++ this[0..(columnId - 1)];
		};
		if (columnId < (this.size - 1)) {
			m = m ++ this[(columnId + 1)..(this.size - 1)];
		};
		m = m.deepCopy();
		^ m.collect {
			arg vector;
			vector.dropElement(rowId);
		}.asMatrix;
	}

	regression {
		// @TODO
		// arg dependentElementId = 0, independentElementId = 1;
		// var variables = Vector[dependentElementId] ++ independentElementId.asVector;
		// var rows = this.rows.at(variables);
		// var independentRows = rows[1..(rows.size - 1)];
		// var dependentSum = rows[0].sum;
		// var independentSums = independentRows.collect({
		// 	arg row;
		// 	row.sum;
		// }).postln;
		//
		// ^ rows;
		// // variables.postln;
	}

	isEigenvalue {
		arg n;
		var lambdaI = Matrix.scalar(this.columnSize, n);
		^ (this - lambdaI).determinant == 0;
	}

	print {
		this.rowSize.do {
			arg i;
			var string = "|  ";
			this.row(i).do {
				arg element;
				string = string ++ element.asString() ++ "  ";
			};
			string.postln;
		};
	}

	asMatrix {
		^ this;
	}

	asArray {
		var a = Array(this.size);
		this.do {
			arg vector;
			a.add(vector.asArray);
		};
		^ a;
	}

	squared {
		^ this * this;
	}

	/**
	 * An override to ensure that binary operations return a matrix and not an Array.
	 * Make sure to use dot products on multiplication of vectors and matrices.
	 */
	performBinaryOp {
		arg aSelector, theOperand, adverb;
		var result;
		if (aSelector === '*') {
			if (theOperand.isKindOf(Vector)) {
				^ this.vectorProduct(theOperand);
			};
			if (theOperand.isKindOf(Matrix)) {
				^ this.matrixProduct(theOperand);
			};
		};
		result = super.performBinaryOp(aSelector, theOperand, adverb);
		^ this.class.newFrom(result);
	}

	/**
	 * An override to ensure that binary operations return a matrix and not an Array.
	 */
	performBinaryOpOnSimpleNumber {
		arg aSelector, theOperand, adverb;
		var result = super.performBinaryOpOnSimpleNumber(aSelector, theOperand, adverb);
		^ this.class.newFrom(result);
	}

}
