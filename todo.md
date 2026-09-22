- define an operator that calculates f(f(f(...f(x)))) with n calls to f.
- implement fast loops.
- implement softer shutdown in grading app, something along those line:
  ```java
  static void after(long millis, Runnable action) {
      Thread thread = new Thread(() -> {
          try {
              Thread.sleep(millis);
          } catch (InterruptedException ignored) {
          }
          action.run();
      });
      thread.setDaemon(true);
      thread.start();
  }

  // Arm the hard deadline first.
  after(15_000, () -> Runtime.getRuntime().halt(1));
  after(10_000, () -> System.exit(0));

  requestWorkersToStop();
  // Return from main.
  ```