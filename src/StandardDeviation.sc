StandardDeviation {
	var < standardDeviation;
	var < mean;
	var < n;
	var confidence;

	*fromArray {
		arg array;
		^ super.new.array = array;
	}

	*new {
		arg standardDeviation, mean, n;
		^ super.new.init(standardDeviation, mean, n);
	}

	array_ {
		arg array;
		mean = array.mean;
		n = array.size;
		standardDeviation = this.pr_standardDeviationFromArray(mean, array);
		confidence = nil;
		^ this;
	}

	pr_standardDeviationFromArray {
		arg mean = 1, array;
		var diffs = array.collect({
      arg element;
      (element - mean).squared;
    });
    ^ (diffs.sum / diffs.size).sqrt;
	}

	init {
		arg a_standardDeviation, a_mean, a_n;

	}

	zScore {
		arg n;
		^ (n - mean) / standardDeviation;
	}

	confidence {
		if (confidence.isNil) {
			confidence = standardDeviation / n.sqrt;
		};
		^ confidence;
	}

}
