package xyz.jerez.io.file;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author liqilin
 * @since 2021/6/20 13:23
 */
public class PathsTest {

    @Test
    public void t1() {
//        写相对路径，默认根路径是模块的根路径
        final Path path = Paths.get("");
        System.out.println(path.toAbsolutePath());
//        如果是目录，就为空
        System.out.println(path.getFileName());
        System.out.println(path.getRoot());
        System.out.println(path.endsWith("examples"));
    }

   @Test
   public void t2() {
//       final Path path = Paths.get("pom.xml");
       final Path path = Paths.get("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-io-examples\\java-file-examples\\pom.xml");
       System.out.println(path.toAbsolutePath());
       System.out.println(path.getFileName());
       System.out.println(path.getRoot());
       System.out.println(path.endsWith("examples"));
       System.out.println(path.endsWith("pom"));
       System.out.println(path.endsWith("xml"));
       System.out.println(path.endsWith("pom.xml"));
//       endsWith 是根据创建Path时的字符串定的，不好用
       System.out.println(path.endsWith("java-file-examples/pom.xml"));
   }

   @Test
   public void t3() throws IOException {
       final Path path = Paths.get("pom.xml");
       final List<String> list = Files.readAllLines(path);
       System.out.println(list);
       Files.lines(path).forEach(System.out::println);
   }

}
