public class DivisionByZero {

    public int divByZeroIntParam(int n) {
        if (n != 0) {
            return 0;
        }
        return 1 / n;
    }

    public long divByZeroLongParam(long n) {
        if (n != 0) {
            return 0;
        }
        return 1 / n;
    }

    public int divByZeroIntParam2(int n) {
        if (n == 0) {
            return 1 / n;
        }
        return 0;
    }

    public long divByZeroLongParam2(long n) {
        if (n == 0) {
            return 1 / n;
        }
        return 0;
    }

    public int divByZeroIntParam3(int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 0) {
            return 0;
        }
        return 1 / n;
    }

    public long divByZeroLongParam3(long n) {
        if (n < 0) {
            return 0;
        }
        if (n > 0) {
            return 0;
        }
        return 1 / n;
    }

    public int divByZeroIntParam4(int n) {
        if (n <= 0) {
            if (n >= 0) {
                return 1 / n;
            }
        }
        return 0;
    }

    public long divByZeroLongParam4(long n) {
        if (n <= 0) {
            if (n >= 0) {
                return 1 / n;
            }
        }
        return 0;
    }

    public int remByZeroIntParam(int n) {
        if (n != 0) {
            return 0;
        }
        return 1 % n;
    }

    public long remByZeroLongParam(long n) {
        if (n != 0) {
            return 0;
        }
        return 1 % n;
    }

    public int remByZeroIntParam2(int n) {
        if (n == 0) {
            return 1 % n;
        }
        return 0;
    }

    public long remByZeroLongParam2(long n) {
        if (n == 0) {
            return 1 % n;
        }
        return 0;
    }

    public int remByZeroIntParam3(int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 0) {
            return 0;
        }
        return 1 % n;
    }

    public long remByZeroLongParam3(long n) {
        if (n < 0) {
            return 0;
        }
        if (n > 0) {
            return 0;
        }
        return 1 % n;
    }

    public int remByZeroIntParam4(int n) {
        if (n <= 0) {
            if (n >= 0) {
                return 1 % n;
            }
        }
        return 0;
    }

    public long remByZeroLongParam4(long n) {
        if (n <= 0) {
            if (n >= 0) {
                return 1 % n;
            }
        }
        return 0;
    }

    /* Negative tests */

    public int divByNonZeroIntParam(int n) {
        return 1 / n;
    }

    public long divByNonZeroLongParam(long n) {
        return 1 / n;
    }

    public int divByNonZeroIntParam2(int n) {
        if (n > 0) {
            return 0;
        }
        return 1 / n;
    }

    public int divByNonZeroIntParam3(int n) {
        if (n >= 0) {
            return 1 / n;
        }
        return 0;
    }

    public int divByNonZeroIntParam4(int n) {
        if (n >= 0) {
            if (n <= 1) {
                return 1 / n;    // n in [0..1]
            }
        }
        return 0;
    }


    public int divByNonZeroIntParam5(int n) {
        if (n > 1) {
            return 0;
        }
        if (n < 0) {
            return 0;
        }
        return 1 / n;    // n in [0..1]
    }
}
