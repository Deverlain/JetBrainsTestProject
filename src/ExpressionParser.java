import java.util.ArrayList;
import java.util.Arrays;

public class ExpressionParser {
  private static final ArrayList<Character> priorityOneCharacters = new ArrayList<>(Arrays.asList('*', '/', '%'));
  private static final ArrayList<Character> priorityTwoCharacters = new ArrayList<>(Arrays.asList('+', '-'));


  public static double calculateFormula(String formula) {
    if (formula.isEmpty() || !validateFormulaForBrackets(formula)) {
      throw new IllegalArgumentException();
    }
    int startOfSubFormula = 0;
    int i = -1;
    while (i + 1 < formula.length()) {
      i++;
      if (formula.charAt(i) == '(') {
        startOfSubFormula = i;
      }
      if (formula.charAt(i) == ')') {
        double value = calculateExpression(formula.substring(startOfSubFormula + 1, i));
        formula = formula.substring(0, startOfSubFormula) + value + formula.substring(i + 1);
        i = -1;
        startOfSubFormula = 0;
      }
    }
    return calculateExpression(formula);
  }


  private static double calculateExpression(String expression) {
    if (expression.isEmpty() || !validateExpression(expression)) {
      throw new IllegalArgumentException();
    }
    expression = calculateOperators(expression, priorityOneCharacters);
    expression = calculateOperators(expression, priorityTwoCharacters);
    return Double.parseDouble(expression);
  }


  private static String calculateOperators(String expression, ArrayList<Character> operators) {
    for (int i = 0; i < expression.length(); i++) {
      if (operators.contains(expression.charAt(i))) {
        int nextSign = i;
        int previousSign = i;
        while (nextSign + 1 < expression.length() &&
            (('0' <= expression.charAt(nextSign + 1) && expression.charAt(nextSign + 1) <= '9')
                || expression.charAt(nextSign + 1) == '.')) {
          nextSign++;
        }
        while (previousSign > 0 &&
            (('0' <= expression.charAt(previousSign - 1) && expression.charAt(previousSign - 1) <= '9')
                || expression.charAt(previousSign - 1) == '.')) {
          previousSign--;
        }
        double firstNumber = Double.parseDouble(expression.substring(previousSign, i));
        double secondNumber = Double.parseDouble(expression.substring(i + 1, nextSign + 1));
        double value = 0;
        if (expression.charAt(i) == '*') {
          value = firstNumber * secondNumber;
        }
        if (expression.charAt(i) == '/') {
          value = firstNumber / secondNumber;
        }
        if (expression.charAt(i) == '%') {
          value = firstNumber % secondNumber;
        }
        if (expression.charAt(i) == '-') {
          value = firstNumber - secondNumber;
        }
        if (expression.charAt(i) == '+') {
          value = firstNumber + secondNumber;
        }
        expression = expression.substring(0, previousSign) + value + expression.substring(nextSign+1);
        i = 0;
      }
    }
    return expression;
  }

  private static boolean validateFormulaForBrackets(String formula) {
    int bracketSum = 0;
    for (int i = 0; i < formula.length(); i++) {
      if (formula.charAt(i) == '(') {
        bracketSum++;
      }
      if (formula.charAt(i) == ')') {
        bracketSum--;
      }
      if (bracketSum < 0) {
        return false;
      }
    }
    return bracketSum == 0;
  }

  private static boolean validateExpression(String formula) {
    return formula.matches("^[-+]?[0-9]*\\.?[0-9]+([-+*/%]?([0-9]*\\.?[0-9]+))*$");
  }

}