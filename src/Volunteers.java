import java.util.*;
import java.io.*;
import javafx.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.image.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.util.Callback;

public class Volunteers extends Application {

    Stage window;
    TableView<Volunteer> tableView;

    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("Volunteers");
        window.setMinHeight(500);
        window.setMinWidth(750);

        // index column
        TableColumn<Volunteer, Number> indexColumn = new TableColumn<Volunteer, Number>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<Number>(tableView.getItems().indexOf(column.getValue())));
        indexColumn.setMinWidth(50);

        // last name column
        TableColumn<Volunteer, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setMinWidth(200);
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last"));

        // first name column
        TableColumn<Volunteer, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setMinWidth(200);
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first"));

        // email column
        TableColumn<Volunteer, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setMinWidth(300);
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // table view
        tableView = new TableView<>();
        ObservableList<Volunteer> volunteers = FXCollections.observableArrayList();
        volunteers = getObserveableList(getVolunteers());
        tableView.setItems(volunteers);
        tableView.getColumns().addAll(indexColumn, lastNameColumn, firstNameColumn, emailColumn);

        // Buttons and Inputs
        Label lastLabel = new Label("Last Name: ");
        TextField lastNameInput = new TextField();
        Label firstLabel = new Label("First Name: ");
        TextField firstNameInput = new TextField();
        Label emailLabel = new Label("Email: ");
        TextField emailInput = new TextField();
        Button add = new Button("Add Volunteer");
        Label allFields = new Label("");
        add.setOnAction(e -> {
            if (lastNameInput.getText() != "" && firstNameInput.getText() != "" && emailInput.getText() != "") {

                try {
                    addVolunteers(lastNameInput.getText(), firstNameInput.getText(), emailInput.getText());
                    tableView.setItems(getObserveableList(getVolunteers()));
                } catch(Exception f) {
                    System.out.println("error creating tableview");
                }

                allFields.setText(firstNameInput.getText()+" "+lastNameInput.getText()+" added.");

                lastNameInput.setText("");
                firstNameInput.setText("");
                emailInput.setText("");

            } else {
                allFields.setText("Please fill out all fields");
            }
        });

        Label toDelete = new Label("Enter index of volunteer to delete");
        TextField indexToDelete = new TextField();
        Label makeSure = new Label("");
        Button delete  = new Button("Remove Volunteer");
        Button confirmDelete = new Button("Confirm");
        Button cancelDelete = new Button("Cancel");
        confirmDelete.setVisible(false);
        confirmDelete.setOnAction(e -> {
            try {
                deleteVolunteer(indexToDelete.getText());
                tableView.setItems(getObserveableList(getVolunteers()));
            } catch(Exception a) {
                makeSure.setText("Error deleting");
                System.out.println(a);
            }
            indexToDelete.setText("");
            makeSure.setText("Deleted");
            confirmDelete.setVisible(false);
            cancelDelete.setVisible(false);
        });
        cancelDelete.setVisible(false);
        cancelDelete.setOnAction(e -> {
            indexToDelete.setText("");
            makeSure.setText("");
            confirmDelete.setVisible(false);
            cancelDelete.setVisible(false);
        });
        delete.setOnAction(e -> {
            if (indexToDelete.getText() != "") {
                // try catch with index. catch is message saying input did not match an index
                try {
                    Boolean isIndexInCSV = checkIndex(Integer.valueOf(indexToDelete.getText()));

                    if (isIndexInCSV) {
                        makeSure.setText(confirmDeleteText(indexToDelete.getText()));
                        confirmDelete.setVisible(true);
                        cancelDelete.setVisible(true);
                    }
                    //deleteVolunteer(Integer.valueOf(indexToDelete.getText()));
                } catch(Exception f) {
                    //makeSure.setText("Enter an index on the table to delete");
                    System.out.println(f);
                }
                // call function to delete index. call function for updated info to display
                // following successful function call display user info and ask 
            } else {makeSure.setText("Enter an index to delete");}
        });

        Button quit = new Button("Quit");
        quit.setOnAction(e -> Platform.exit());

        // Gridpane
        GridPane theButtons = new GridPane();
        theButtons.setPadding(new Insets(10, 10, 10, 10));
        theButtons.setVgap(5);
        theButtons.setHgap(5);
        theButtons.setAlignment(Pos.CENTER);

        theButtons.add(lastLabel, 0, 0);
        theButtons.add(lastNameInput, 1, 0);
        theButtons.add(firstLabel, 2, 0);
        theButtons.add(firstNameInput, 3, 0);
        theButtons.add(emailLabel, 4, 0);
        theButtons.add(emailInput, 5, 0);

        theButtons.add(add, 0, 1);
        theButtons.add(allFields, 1, 1);

        theButtons.add(toDelete, 0, 2);
        theButtons.add(indexToDelete, 1, 2);

        theButtons.add(delete, 0, 3);
        theButtons.add(makeSure, 1, 3);
        theButtons.add(confirmDelete, 2, 3);
        theButtons.add(cancelDelete, 3, 3);

        theButtons.add(quit, 0, 4);

        // vBox
        VBox vBox = new VBox();
        vBox.getChildren().addAll(tableView, theButtons);

        // Scene
        Scene scene = new Scene(vBox);
        window.setScene(scene);
        window.show();
    }

    // Convert linkedList to Observeable list to display in tableView
    public ObservableList<Volunteer> getObserveableList(LinkedList<Volunteer> linkedVolunteers) {
        ObservableList<Volunteer> volunteers = FXCollections.observableArrayList();

        for (int i = 0; i < linkedVolunteers.size(); i++) {
            String lastName = String.valueOf(linkedVolunteers.get(i).getLast());
            String firstName = String.valueOf(linkedVolunteers.get(i).getFirst());
            String email = String.valueOf(linkedVolunteers.get(i).getEmail());
            volunteers.add(new Volunteer<String>(lastName, firstName, email));
        }

        return volunteers;        
    }

    // create linkedList from csv file
    public LinkedList<Volunteer> getVolunteers() throws Exception {

        //ObservableList<Volunteer> volunteers = FXCollections.observableArrayList();
        LinkedList<Volunteer> volunteers = new LinkedList<Volunteer>();

        BufferedReader reader = new BufferedReader(new FileReader("./src/Volunteers2.csv"));
        //BufferedReader lineCounter = new BufferedReader(new FileReader("./src/Volunteers.csv"));

        // Count lines in csv file
        String line = null;
        Scanner scanner = null;

        line = null;
        scanner = null;

        while((line = reader.readLine()) != null) {

            scanner = new Scanner(line);
            scanner.useDelimiter(",");
            String[] tempArray = new String[3];
            int index = 0;

            // get individual strings
            while (scanner.hasNext()) {
                String data = scanner.next();

                // Kept getting an index out of bounds error when i set up tempArray[index] = data. Tried to figure it out but didnt so I did the below instead
                if (index == 0) {
                    tempArray[0] = data;
                } else if (index == 1) {
                    tempArray[1] = data;
                } else if (index == 2) {
                    tempArray[2] = data;
                }
                index++;
            }
            volunteers.add(new Volunteer<String>(tempArray[0], tempArray[1], tempArray[2]));
        }
        return volunteers;
    }

    // add new object to csv file. Takes inputs from user in ui
    public void addVolunteers(String last, String first, String email) throws Exception {

        LinkedList<Volunteer> volunteers = getVolunteers();
        volunteers.add(new Volunteer<String>(last, first, email));
        // call function to alphabetize new entry

        // Objects for writing to csv file
        FileWriter writer = new FileWriter("./src/Volunteers2.csv");
        BufferedWriter bw = new BufferedWriter(writer);

        for (int i = 0; i < volunteers.size(); i++) {
            String lastName = String.valueOf(volunteers.get(i).getLast());
            String firstName = String.valueOf(volunteers.get(i).getFirst());
            String emailFromArray = String.valueOf(volunteers.get(i).getEmail());
            String tempLine = lastName+","+firstName+","+emailFromArray;
            bw.write(tempLine);
            bw.newLine();
        }

        bw.close();
        writer.close();
    }

    public Boolean checkIndex(int index) throws Exception {
        BufferedReader lineCounter = new BufferedReader(new FileReader("./src/Volunteers.csv"));
        Boolean inIndex;

        // Count lines in csv file
        String line = null;
        int countTotalLines = 0;
        while ((line = lineCounter.readLine()) != null) {
            countTotalLines ++;
        }

        if (index >= 0 && index <= countTotalLines) {  inIndex = true; } else {  inIndex = false; }

        return inIndex;
    }

    public String confirmDeleteText(String index) throws Exception {

        LinkedList<Volunteer> volunteers = new LinkedList<Volunteer>();

        BufferedReader reader = new BufferedReader(new FileReader("./src/Volunteers2.csv"));

        String line = null;
        Scanner scanner = null;
        int indexToDelete = Integer.valueOf(index);

        while((line = reader.readLine()) != null) {

            scanner = new Scanner(line);
            scanner.useDelimiter(",");
            String[] tempArray = new String[3];

            // get individual strings
            while (scanner.hasNext()) {
                String data = scanner.next();
                int index1 = 0;

                // Kept getting an index out of bounds error when i set up tempArray[index] = data. Tried to figure it out but didnt so I did the below instead
                if (index1 == 0) {
                    tempArray[0] = data;
                } else if (index1 == 1) {
                    tempArray[1] = data;
                } else if (index1 == 2) {
                    tempArray[2] = data;
                }
                index1++;
            }
            volunteers.add(new Volunteer<String>(tempArray[0], tempArray[1], tempArray[2]));
        }
        String last = String.valueOf(volunteers.get(indexToDelete).getLast());
        String first = String.valueOf(volunteers.get(indexToDelete).getFirst());
        String email = String.valueOf(volunteers.get(indexToDelete).getEmail());

        return "Delete "+first+" "+last+", "+email+"?";
    }

    public void deleteVolunteer(String index) throws Exception {

        LinkedList<Volunteer> volunteers = getVolunteers();
        int indexToDelete = Integer.valueOf(index);
        //volunteers.add(new Volunteer<String>(last, first, email));
        // call function to alphabetize new entry

        // Objects for writing to csv file
        FileWriter writer = new FileWriter("./src/Volunteers2.csv");
        BufferedWriter bw = new BufferedWriter(writer);

        for (int i = 0; i < indexToDelete; i++) {
            String lastName = String.valueOf(volunteers.get(i).getLast());
            String firstName = String.valueOf(volunteers.get(i).getFirst());
            String emailFromArray = String.valueOf(volunteers.get(i).getEmail());
            String tempLine = lastName+","+firstName+","+emailFromArray;
            bw.write(tempLine);
            bw.newLine();
        }

        indexToDelete += 1;

        for (int i = indexToDelete; i < volunteers.size(); i++) {
            String lastName = String.valueOf(volunteers.get(i).getLast());
            String firstName = String.valueOf(volunteers.get(i).getFirst());
            String emailFromArray = String.valueOf(volunteers.get(i).getEmail());
            String tempLine = lastName+","+firstName+","+emailFromArray;
            bw.write(tempLine);
            bw.newLine();
        }

        bw.close();
        writer.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}