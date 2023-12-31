package by.faeton.lyceumteacherbot.services;

import by.faeton.lyceumteacherbot.model.User;
import by.faeton.lyceumteacherbot.utils.SheetListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static by.faeton.lyceumteacherbot.utils.DefaultMessages.NOT_AVAILABLE;


@Service
@RequiredArgsConstructor
public class SheetService {

    private final SheetListener sheetListener;
    private final UserService userService;

    public String getStudentMarks(User user) {
        Optional<ArrayList<ArrayList<String>>> sheetDateLine = sheetListener.getSheetList(user.getList(), userService.getDateColumn());
        Optional<ArrayList<ArrayList<String>>> sheetTypeLine = sheetListener.getSheetList(user.getList(), userService.getTypeOfWorkColumn());
        Optional<ArrayList<ArrayList<String>>> sheetMarksLine = sheetListener.getSheetList(user.getList(), userService.getMarksColumn(user));
        String marks = linesToString(sheetDateLine, sheetTypeLine, sheetMarksLine);
        return marks;
    }

    public String getStudentQuarterMarks(User user) {
        Optional<ArrayList<ArrayList<String>>> sheetDateLine = sheetListener.getSheetList(user.getList(), userService.getQuarterNameColumn());
        Optional<ArrayList<ArrayList<String>>> sheetTypeLine = sheetListener.getSheetList(user.getList(), userService.getTypeOfQuarterColumn());
        Optional<ArrayList<ArrayList<String>>> sheetQuarterLine = sheetListener.getSheetList(user.getList(), userService.getQuarterMarksColumn(user));
        String marks = linesToString(sheetDateLine, sheetTypeLine, sheetQuarterLine);
        return marks;
    }

    public String getStudentLaboratoryNotebook(User user) {
        String field = userService.getLaboratoryNotebookColumn(user);
        String cell = sheetListener.getCell(user.getList(), field);
        return cell;
    }

    public String getStudentTestNotebook(User user) {
        String field = userService.getTestNotebookColumn(user);
        String cell = sheetListener.getCell(user.getList(), field);
        return cell;
    }

    public String linesToString(Optional<ArrayList<ArrayList<String>>>... values) {
        ArrayList<ArrayList<String>> returnedList = new ArrayList<>();

        for (Optional<ArrayList<ArrayList<String>>> firstValue : values) {
            firstValue.ifPresent(arrayLists -> returnedList.add(arrayLists.get(0)));
        }

        String returnedText = "";
        if (returnedList.size() > 1) {
            int lastNumberListOfValues = returnedList.size() - 1;
            ArrayList<String> lastList = returnedList.get(lastNumberListOfValues);
            int countElementsInLastListOfValues = lastList.size();
            for (int i = 0; i < countElementsInLastListOfValues; i++) {
                if (lastList.get(i) != null && !lastList.get(i).equals("")) {
                    for (ArrayList<String> strings : returnedList) {
                        if (i < strings.size()) {
                            returnedText = returnedText + strings.get(i) + " ";
                        }
                    }
                    returnedText = returnedText + '\n';
                }
            }
        } else {
            returnedText = returnedText + NOT_AVAILABLE;
        }
        return returnedText;
    }

}

