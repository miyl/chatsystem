package chat.libs.protocol.lexer;

import chat.libs.protocol.ChatLanguage;
import chat.libs.protocol.Language;

import java.util.List;

// File writing (for testing)
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LexerTester {

  public static void main(String[] args) {
    System.out.println("Running Lexer Tester");

    String testText1 = "DATA mjav mbq:woot 123 ";
    String testText2 = "DATA mjav ";
    String testText3 = "mjav";
    String testText4 = " JOIN ";

    Language lang = new ChatLanguage();
    Lexer l = new Lexer( lang.getTokenTypes() );

    System.out.println("About to Lex");

    List<Token> tokens = l.lex(testText1);

    if (tokens == null) {
      System.out.println("The input is a different language than the one expressed by the token types.");
      System.out.println("Exiting!");
      System.exit(-1);
    }

    System.out.println("Done: Lexing");
    System.out.println("Writing to file");
    writeTokensToFile(tokens);
    System.out.println("Done: Writing to file");
  }

  public static void writeTokensToFile(List<Token> tokens) {

    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter("tokens.txt", false)); // Overwrite if existing instead of append

      for (var t : tokens) {
        bw.write( "Type: " + t.getType().getName() + "  |  Value: " + t.getValue() + "\n");
      }
      bw.flush();
      bw.close();
    }
    catch (IOException e) { e.printStackTrace(); }
  }
  
}
