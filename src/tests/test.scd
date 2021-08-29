#! /usr/local/bin/sclang -D

(
var testClass = VectorAbstractTest;
var timeout = 10;
var timeoutIncrement = 0.5;
var report = false;
  {
    var testCount = 0;
    var classOption = "--class".asSymbol;
    var timeoutOption = "--timeout".asSymbol;
    var reportOption = "--report".asSymbol;
    var args = thisProcess.argv.collect {
      arg p;
      p.asSymbol;
    };

    if (args.includes(classOption)) {
      testClass = args[args.indexOf(classOption) + 1].asClass();
    };
    if (args.includes(timeoutOption)) {
      timeout = args[args.indexOf(timeoutOption) + 1].asFloat();
    };
    if (args.includes(reportOption)) {
      report = true;
    };

    try {
      // "Running subtests for % class...".format(testClass).postln;
      Process.tailCallOptimize = false;
      forkIfNeeded {
        var classes = testClass.allSubclasses();
        classes = classes.add(testClass);
        testClass.reportPasses_(false);
        classes.do { |subClass|
          fork {
            var status = false;
            var c = 0;
            testCount = testCount + 1;
            fork {
              subClass.run(false, false);

              status = true;
            };
            while ({status.not()}) {
              (timeoutIncrement).wait;
              c = c + 1;
              if ((timeoutIncrement * c) > timeout) {
                (subClass.asString() ++ " is hanging").error();
                // status = true;
              };
            };
            if (status) {
              testCount = testCount - 1;
              if (testCount == 0) {
                if (report) {
                  testClass.report;
                };
                0.exit;
              };
            };

          };
        };
      };
    } {
      arg error;
      "Completed with errors".postln;
      error.reportError;
      error.class.postln;
      0.1.wait;
      0.exit;
    };
  }.fork;
)
