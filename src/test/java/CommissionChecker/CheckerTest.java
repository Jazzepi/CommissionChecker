//package CommissionChecker;
//
//import org.apache.commons.logging.Log;
//import org.jsoup.Connection;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Answers;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class CheckerTest {
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    JSoupWrapper jSoupWrapperMock;
//    @Mock
//    Elements elementsMock;
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    Element elementMock;
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    Element elementMock2;
//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    Element elementMock3;
//    @Mock
//    Log loggerMock;
//
//    @Test
//    public void checker_filters_results() throws IOException {
//        //We are always logged in
//        when(jSoupWrapperMock.connect("https://www.furaffinity.net/login/?ref=http://www.furaffinity.net/")
//                .cookies(anyMapOf(String.class, String.class))
//                .timeout(anyInt())
//                .method(Connection.Method.POST)
//                .execute().parse().select("#my-username").isEmpty()).thenReturn(false);
//
//        //Return fake data used for the journal
//        when(jSoupWrapperMock.connect("http://www.furaffinity.net/msg/others/")
//                .cookies(anyMapOf(String.class, String.class))
//                .timeout(anyInt())
//                .get()
//                .select("#messages-journals .message-stream li:not(.section-controls)")).thenReturn(elementsMock);
//        when(elementsMock.size()).thenReturn(1);
//        when(elementsMock.iterator()).thenReturn(new ArrayList<Element>() {{
//            add(elementMock);
//            add(elementMock2);
//            add(elementMock3);
//        }}.iterator());
//        when(elementMock.childNode(4).childNode(0).toString()).thenReturn("tehsean");
//        when(elementMock.childNode(2).childNode(0).toString()).thenReturn("mock journal entry with keyword commission in it");
//        when(elementMock2.childNode(4).childNode(0).toString()).thenReturn("mock name not on the list");
//        when(elementMock2.childNode(2).childNode(0).toString()).thenReturn("mock journal entry with keyword commission in it");
//        when(elementMock3.childNode(4).childNode(0).toString()).thenReturn("tehsean");
//        when(elementMock3.childNode(2).childNode(0).toString()).thenReturn("mock journal entry without keyword in it");
//        Checker checkerLegacy = new Checker();
//
//        List<Checker.JournalEntry> result = checkerLegacy.checkFA();
//
//        assertEquals(1, result.size());
//        assertEquals("tehsean", result.get(0).getUsername());
//        assertEquals("mock journal entry with keyword commission in it", result.get(0).getJournalName());
//    }
//
//}
