package test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class App {
  public static void main(String[] args) {
    Application.launch(Main.class, args);
  }

  public static class Main extends Application {
    private Stage stage;
    private VBox root;
    private TextArea text;
    private MenuBar menu;

    private String fName = "";
    private Matcher currentRegex = null;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
      stage = primaryStage;
      text  = new TextArea();
      menu = new MenuBar();
      root = new VBox();
      var scene = new Scene(root, 640, 480);

      text.setPromptText("");
      VBox.setVgrow(text, Priority.ALWAYS);

      var menuFile = new Menu("_File");
      var menuEdit = new Menu("_Edit");

      var menuFileNew = new MenuItem("_New");
      var menuFileOpen = new MenuItem("_Open"); 
      var menuFileSave = new MenuItem("_Save");
      var menuFileClose = new MenuItem("_Close");
      var menuEditFind = new MenuItem("_Find");
      var menuEditReplace = new MenuItem("_Find and replace");

      menuEdit.getItems().addAll(
          menuEditFind,
          menuEditReplace
      );
      menuFile.getItems().addAll(
          menuFileNew,
          menuFileOpen,
          menuFileSave,
          new SeparatorMenuItem(),
          menuFileClose
      );
      menu.getMenus().addAll(
          menuFile,
          menuEdit
      );

      menuFileNew.setOnAction(act -> { newFile(); });
      menuFileOpen.setOnAction(act -> { openFile(); });
      menuFileSave.setOnAction(act -> { saveFile(); });
      menuFileClose.setOnAction(act -> { closeFile(); });
      menuEditFind.setOnAction(act -> { findStart(); });
      menuEditReplace.setOnAction(act -> { replaceStart(); });

      root.getChildren().addAll(menu, text);

      stage.setScene(scene);
      stage.setTitle("Notepad 1.0");
      stage.setMaximized(true);
      stage.getIcons().add(new Image("/assets/icon.png"));

      stage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyevent -> {
        final var CtrlN  = new KeyCodeCombination(KeyCode.N , KeyCombination.CONTROL_DOWN);
        final var CtrlO  = new KeyCodeCombination(KeyCode.O , KeyCombination.CONTROL_DOWN);
        final var CtrlS  = new KeyCodeCombination(KeyCode.S , KeyCombination.CONTROL_DOWN);
        final var CtrlQ  = new KeyCodeCombination(KeyCode.Q , KeyCombination.CONTROL_DOWN);
        final var CtrlW  = new KeyCodeCombination(KeyCode.W , KeyCombination.CONTROL_DOWN);
        final var CtrlF  = new KeyCodeCombination(KeyCode.F , KeyCombination.CONTROL_DOWN);
        final var CtrlH  = new KeyCodeCombination(KeyCode.H , KeyCombination.CONTROL_DOWN);
        final var CtrlF4 = new KeyCodeCombination(KeyCode.F4, KeyCombination.CONTROL_DOWN);
        final var     F3 = new KeyCodeCombination(KeyCode.F3, KeyCombination.SHORTCUT_ANY);
        if (CtrlQ.match(keyevent) || CtrlW.match(keyevent) || CtrlF4.match(keyevent)) {
          closeFile();
        }
        else if (CtrlN .match(keyevent)) {
          newFile();
        }
        else if (CtrlO .match(keyevent)) {
          openFile();
        }
        else if (CtrlS .match(keyevent)) {
          saveFile();
        }
        else if (CtrlF .match(keyevent)) {
          findStart();
        }
        else if (F3.match(keyevent)) {
          findNext();
        }
        else if (CtrlH .match(keyevent)) {
          replaceStart();
        }
      });

      stage.show();
    }

    private void findStart() {
      Platform.runLater(() -> {
        System.out.println("Hi");
        var dialog = new TextInputDialog("");
        dialog.setTitle("Find");
        dialog.showAndWait();
        System.out.println("Searching " + dialog.getEditor().getText() + "...");
        currentRegex = Pattern.compile(dialog.getEditor().getText()).matcher(text.getText());
        findNext();
      });
    }

    private void findNext() {
      Platform.runLater(() -> {
        if (currentRegex == null) { 
          return;
        }

        if (!currentRegex.find()) {
          currentRegex.reset();
          findNext();
        }

        text.selectRange(currentRegex.end(), currentRegex.start());
      });
    }

    private void replaceStart() {
      throw new java.lang.UnsupportedOperationException();
    }

    private void closeFile() {
      javafx.application.Platform.exit();
    }

    private void newFile() {
      fName = "";
      text.setText("");
    }

    private void saveFile() {
      try {
        FileOutputStream out;
        if (fName == "") {
          var selected = new FileChooser().showSaveDialog(stage);
          out = new FileOutputStream(selected);
        } else {
          out = new FileOutputStream(fName);
        }
        out.write(text.getText().getBytes(StandardCharsets.UTF_8));
        out.close();
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }

    private void openFile() {
      try {
        var selected = new FileChooser().showOpenDialog(stage);
        var sb = new StringBuilder();
        var r = new InputStreamReader(new FileInputStream(selected), "UTF-8");
        char[] buf = new char[1024];
        int amt = r.read(buf);
        while (amt > 0) {
          sb.append(buf, 0, amt);
          amt = r.read(buf);
        }
        fName = selected.getAbsolutePath();
        text.setText(sb.toString());
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  }

}
