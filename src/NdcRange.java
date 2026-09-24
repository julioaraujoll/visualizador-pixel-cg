public enum NdcRange {
    ZERO_UM("[0,1] x [0,1]"),
    MENOS_UM_UM("[-1,1] x [-1,1]");

    private final String label;
    NdcRange(String label) {
        this.label = label;
    }
    @Override public String toString() {
        return label;
    }
}
