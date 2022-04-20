import org.w3c.dom.Element;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        //TESTING GROUND
        Scanner input = new Scanner(System.in);
        Repository repo = new Repository("./ArticleXML1.xml");

        repo.appendToRepository(repo.xmlController.readArticleFromXML());

        System.out.println("Normal---------------------");
        repo.print();

        System.out.println("Title---------------------");

        repo.sortByTitle();
        repo.print();

        System.out.println("Author---------------------");

        repo.sortByAuthor();
        repo.print();

        System.out.println("Date---------------------");

        repo.sortByDate();
        repo.print();

        repo.xmlController.writeArticleToXML(repo);

//        for (Element element : list) {
//            System.out.println(element.getElementsByTagName("id").item(0) + " " +
//            element.getElementsByTagName("title").item(0) + " " +
//            element.getElementsByTagName("author").item(0) + " " +
//            element.getElementsByTagName("publishDate").item(0) + " " +
//            element.getElementsByTagName("content").item(0) + " \n");
//            for (int i = 0; i < list.size(); i++)
//                System.out.println(element.getElementsByTagName("title").item(i));
//        }

        //input.close();
    }
}
