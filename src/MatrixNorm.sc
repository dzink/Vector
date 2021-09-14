MatrixNorm {
	*columnNormalize {
		arg m, algo = \l2;
		^ m.collect {
			arg vector;
			vector.perform(algo);
		}.asMatrix;
	}

	*froNorm {
		arg m;
		var f = m.flatten;
		^ f.collect({
			arg n;
			n.squared;
		}).sum.sqrt;
	}

	*infNorm {
		arg m;
		var max = 0;
		m.rows().do {
			arg vector;
			var sum = vector.abs().sum();
			max = max(sum, max);
		};
		^ max
	}

	*l1 {
		arg m;
		var max = 0;
		m.do {
			arg vector;
			var sum = vector.abs().sum();
			max = max(sum, max);
		};
		^ max
	}
}
