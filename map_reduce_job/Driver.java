public class Driver {

    public static void main(String[] args) throws Exception {

        if (args == null || args.length < 4) {
            String title = "In Driver.main: main function needs at least 4 arguments:\n";
            String args0 = "args0: dir of transition.txt\n";
            String args1 = "args1: dir of PageRank.txt\n";
            String args2 = "args2: dir of MatrixCellMultiplication results\n";
            String args3 = "args3: times of iteration\n";
            String args4 = "args4: value of beta\n";
            String end = "If only receives 4 args, the last argument will use 0.2f by default.";
            throw new Exception(title + args0 + args1 + args2 + args3 + args4 + end);
        }

        MatrixCellMultiplication multiplication = new MatrixCellMultiplication();
        MatrixCellSummation summation = new MatrixCellSummation();

        // args0: dir of transition.txt
        // args1: dir of PageRank.txt
        // args2: dir of MatrixCellMultiplication results
        // args3: times of iteration
        // args4: value of beta

        String transitionMatrixDir = args[0];
        String pageRankMatrixDir = args[1];
        String multiplicationResultsDir = args[2];
        int iterationTimes = Integer.parseInt(args[3]);
        String beta = args.length < 5 ? "0.2" : args[4];

        for (int i = 0; i < iterationTimes; ++i) {
            String[] args1 = {transitionMatrixDir, pageRankMatrixDir + i, 
                              multiplicationResultsDir + i, beta};
            multiplication.main(args1);

            String[] args2 = {multiplicationResultsDir + i, pageRankMatrixDir + i,
                              pageRankMatrixDir + (i + 1), beta};
            summation.main(args2);
        }
    }

}