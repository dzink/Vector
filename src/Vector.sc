Vector[float] : FloatArray {

  *unit {
    arg size = 2, x = 0;
    var v = this.new(size);
    v[x] = 1;
    ^ v;
  }

  //
  // clear {
  //   for (0, this.maxSize, {
  //     arg n;
  //     [\clear, n].postln;
  //     this[n] = 0;
  //   })
  //   ^ this;
  // }

  /**
   * For now let's just do the dot.
   */
  inner {
    arg other;
    ^ this.dot(other);
  }

  dot {
    arg other;
    var sum = 0;
    this.do {
      arg element, i;
      var otherElement = other[i];
      sum = sum + (element * otherElement);
    };
    ^ sum;
  }

  * {
    arg other;
    if (other.isKindOf(Matrix)) {
      ^ Matrix[this] * other;
    };
    ^ super * other;
  }

  /**
   * If taking the dot of itself, we can calculate a bit faster.
   */
  selfDot {
    var sum = 0;
    this.do {
      arg element;
      sum = sum + element.squared;
    };
    ^ sum;
  }

  /**
   * L1 (Manhattan norm).
   */
  l1 {
    var sum = 0;
    this.do {
      arg element;
      sum = sum + element.abs;
    };
    ^ sum;
  }

  /**
   * L2 (Euclidean norm)
   */
  l2 {
    ^ this.dot(this).sqrt;
  }

  /**
   * Max norm - returns the highest absolute element.
   */
  maxNorm {
    var abs = this.abs;
    ^ abs[abs.maxIndex];
  }

  /**
   * Calculates the cos angle difference between 2 vectors.
   */
  angle {
    arg other;
    var inner = this.inner(other);
    var normProduct = this.l2() * other.l2();
    ^ inner / normProduct;
  }

  performBinaryOp {
    arg aSelector, theOperand, adverb;
    var result = super.performBinaryOp(aSelector, theOperand, adverb);
    ^ this.class.newFrom(result);
  }

  performBinaryOpOnSimpleNumber {
    arg aSelector, theOperand, adverb;
    var result = super.performBinaryOpOnSimpleNumber(aSelector, theOperand, adverb);
    ^ this.class.newFrom(result);
  }

  == {
    arg other, sensitivity = 0.0001;
    if (other.isKindOf(Vector).not) {
      ^ false;
    };
    if (this.size !== other.size) {
      ^ false;
    };
    this.do {
      arg element, row;
      if ((element - other[row]).abs > sensitivity) {
        ^ false;
      };
    };
    ^ true;
  }

  != {
    arg other, sensitivity = 0.0001;
    ^ this.perform('==', other, sensitivity).not();
  }
}
