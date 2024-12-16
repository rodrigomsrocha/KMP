import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KMP {
  public Optional<List<Long>> search(RandomAccessFile file, byte[] pattern) throws IOException {
    long fileSize = file.length();
    int patternLength = pattern.length;
    ArrayList<Long> results = new ArrayList<>();
    int[] lps = buildLongestPrefixSuffix(pattern);
    long i = 0;
    int j = 0;

    while (i < fileSize) {
      file.seek(i);
      int fileByte = file.read();
      if (fileByte == -1) {
        break;
      }

      // If characters match, move both pointers forward
      if (fileByte == pattern[j]) {
        i++;
        j++;

        // If the entire pattern is matched store the start index in result
        if (j == patternLength) {
          results.add(i - j);

          // Use LPS of previous index to skip unnecessary comparisons
          j = lps[j - 1];
        }
      } else {
        if (j != 0) {
          j = lps[j - 1];
        } else {
          i++;
        }
      }
    }

    return results.isEmpty() ? Optional.empty() : Optional.of(results);
  }

  private int[] buildLongestPrefixSuffix(byte[] pattern) {
    int lenght = 0;
    int[] longestPrefixSuffix = new int[pattern.length];
    longestPrefixSuffix[0] = 0;

    int i = 1;

    while (i < pattern.length) {
      if (pattern[i] == pattern[lenght]) {
        lenght++;
        longestPrefixSuffix[i] = lenght;
        i++;
      } else {
        if (lenght != 0) {
          lenght = longestPrefixSuffix[lenght - 1];
        } else {
          longestPrefixSuffix[i] = 0;
          i++;
        }
      }
    }

    return longestPrefixSuffix;
  }

  public static void main(String[] args) {
    String fileName = "example";

    String fileContent = "Este é um exemplo de busca com o algoritmo KMP. O KMP é eficiente.";
    String pattern = "KMP";

    try {
      File file = new File(fileName);
      try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
        raf.setLength(0);
        raf.write(fileContent.getBytes());

        KMP kmp = new KMP();
        Optional<List<Long>> result = kmp.search(raf, pattern.getBytes());

        if (result.isPresent()) {
          System.out.println("Padrão encontrado nas posições: " + result.get());
        } else {
          System.out.println("Padrão não encontrado no arquivo.");
        }
      }
    } catch (IOException e) {
      System.err.println("Erro ao manipular o arquivo: " + e.getMessage());
    }
  }
}