package test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import java.util.ArrayList;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;


public class Sample implements EntryPoint, ClickHandler
{
   private static class MyWorker
   {
      private final String name;
      private final String username;
      private final String department;
      private final int id;
      
      public MyWorker(String nameStr, String user, String dept, int id)
      {
         name = nameStr;
         username = user;
         department = dept;
         this.id = id;
      }
   }
   ArrayList<MyWorker> workers = new ArrayList<MyWorker>();
   JsArray<Worker> jsonData;
   VerticalPanel mainPanel = new VerticalPanel();
   Button loginButton = new Button("Login");
   TextBox userBox = new TextBox();
   PasswordTextBox passBox = new PasswordTextBox();
   String urlBase = "http://localhost:3000";
   public void onModuleLoad()
   {
      RootPanel.get().add(mainPanel);
      loginButton.addClickHandler(this); // must be here not in login
      login();
      //String url = "http://localhost:3000/workers.json";
      //getRequest(url);
   }
   public void onClick(ClickEvent e)
   {
      Object source = e.getSource();
      if (source == loginButton) {
         //mainPanel.clear();
         String url = urlBase + "/workers/login";
         String encData = URL.encode("username") + "=" +
            URL.encode(userBox.getText()) + "&" +
            URL.encode("password") + "=" +
            URL.encode(passBox.getText());
         postRequest(url,encData);
      }
   }
   private void postRequest(String url, String data)
   {
      final RequestBuilder rb =
         new RequestBuilder(RequestBuilder.POST,url);
      rb.setHeader("Content-type", 
        "application/x-www-form-urlencoded");
      try {
         rb.sendRequest(data, new RequestCallback()
         {
            public void onError(final Request request,
               final Throwable exception)
            {
               Window.alert(exception.getMessage());
            }
            public void onResponseReceived(final Request request,
               final Response response)
            {
               int id = Integer.parseInt(response.getText().trim());
               if (id == 1) {
                  mainPanel.clear();
                  String url = urlBase + "/workers.json";
                  getRequest(url);
               }
               else {
                  userBox.setText("");
                  passBox.setText("");
               }
            }
         });
      }
      catch (final Exception e) {
         Window.alert(e.getMessage());
      }
   }
   private void getRequest(String url)
   {
      final RequestBuilder rb =
         new RequestBuilder(RequestBuilder.GET,url);
      try {
         rb.sendRequest(null, new RequestCallback()
         {
            public void onError(final Request request,
               final Throwable exception)
            {
               Window.alert(exception.getMessage());
            }
            public void onResponseReceived(final Request request,
               final Response response)
            {
               String text = response.getText();
               showWorkersCellTable(text);
            }
         });
      }
      catch (final Exception e) {
         Window.alert(e.getMessage());
      }
   }
   private JsArray<Worker> getJSONData(String json)
   {
      return JsonUtils.safeEval(json);
   }
   private void showWorkersCellTable(String json)
   {
      jsonData = getJSONData(json);
      Worker worker = null;
      for (int i = 1; i < jsonData.length(); i++) {
         worker = jsonData.get(i);
         String name = worker.getName();
         String username = worker.getUsername();
         String department = worker.getDepartment();
         int id = worker.getId();
         MyWorker w = new MyWorker(name,username,department,id);
         workers.add(w);
      }
      TextColumn<MyWorker> nameCol =
         new TextColumn<MyWorker>()
         {
            @Override
            public String getValue(MyWorker worker)
            {
               return worker.name;
            }
         };
      TextColumn<MyWorker> usernameCol =
         new TextColumn<MyWorker>()
         {
            @Override
            public String getValue(MyWorker worker)
            {
               return worker.username;
            }
         };
      TextColumn<MyWorker> deptCol =
         new TextColumn<MyWorker>()
         {
            @Override
            public String getValue(MyWorker worker)
            {
               return worker.department;
            }
         };
      Column<MyWorker, Number> idCol =
         new Column<MyWorker, Number>(new NumberCell())
         {
            @Override 
            public Number getValue(MyWorker worker)
            {
               return (Number) worker.id;
            }
         };
      CellTable<MyWorker> table =
         new CellTable<MyWorker>();
      table.addColumn(idCol,"ID");
      table.addColumn(nameCol,"Name");
      table.addColumn(usernameCol,"Username");
      table.addColumn(deptCol,"Department");
      table.setRowCount(workers.size(),true);
      table.setRowData(0,workers);
      mainPanel.add(table);
   }
   private void login()
   {
      VerticalPanel loginPanel = new VerticalPanel();
      HorizontalPanel row1 = new HorizontalPanel();
      Label userLabel = new Label("Username: ");
      row1.add(userLabel);
      row1.add(userBox);
      loginPanel.add(row1);
      HorizontalPanel row2 = new HorizontalPanel();
      Label passLabel = new Label("Password: ");
      row2.add(passLabel);
      row2.add(passBox);
      loginPanel.add(row2);
      loginPanel.add(loginButton);
      mainPanel.add(loginPanel);
   }
}