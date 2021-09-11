package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChatroomController implements Initializable {

	@FXML
	private Button exit;
	@FXML
	private Label userCount;
	@FXML
	private TextField userInput;
	@FXML
	private Button sendMessage;
	@FXML
	private TextArea chatArea;
	@FXML
	private ListView<String> listview;
	@FXML
	private Button game;
	@FXML
	private Button rulePop;
	@FXML
	public Label myLabel;
	@FXML
	public ComboBox<String> combobox;
	@FXML
	public Button voteStart;
	
	
	ObservableList<String> members = FXCollections.observableArrayList();
	ClientMain clientSource;
	String nickname;
	String regex = "[@#$%^&*():\"{}|<>]"; //홍경인


	@FXML
	void gameStart() {
		if (game.getText().equals("게임시작")) {
			
			clientSource.startLiarGame();
			
			Platform.runLater(() -> {
				game.setDisable(true);
			});
		} else {
			clientSource.endLiarGame();
			Platform.runLater(() -> {
				game.setText("게임시작");// 버튼에 쓰인 정보 바꿔주기
			});
		}
	}
	@FXML
	public void logout() {
		String message = ":종료할겁니다." + clientSource.nickname;
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = clientSource.ChatSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					clientSource.stopClient();
				}
			}
		};
		thread.start();
	}

	public ChatroomController() {
		System.out.println("Controller Constuctor called");
	}
	
	@FXML
	public void Send() {
		Alert alert = new Alert(AlertType.INFORMATION);
		String temp=userInput.getText();
		if (temp.length() != temp.replaceAll(regex, "").length()) {
			alert.setHeaderText("알림");
			alert.setContentText("특수문자는 채팅 중에 사용이 불가능합니다.");
			alert.show();
		}
		else {
			String message = clientSource.nickname + ": " + temp;
			Thread thread = new Thread() {
				public void run() {
					try {
						OutputStream out = clientSource.ChatSocket.getOutputStream();
						byte[] buffer = message.getBytes("UTF-8");
						out.write(buffer);
						out.flush();
					} catch (Exception e) {
						clientSource.stopClient();
					}
				}
			};
			thread.start();
			userInput.clear(); // 지울 예정
		}
	}

	

	public void closeStage() {
		Stage stage11 = (Stage) exit.getScene().getWindow();
		Platform.runLater(() -> {
			stage11.close();
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		clientSource = new ClientMain();
		listview.setItems(FXCollections.observableArrayList());
		exit.setOnAction(e -> {
			logout();
			exitPressed(e);
		});
		voteStart.setOnAction(e -> {
			voteConfirm();
		});
		rulePop.setOnAction(e->{
			rulePopPage();
		});


		userInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ENTER) {
					Send();
				}
			}

		});
	}

	String message;
	String vectorName;
	String vec = "";

	public void receive() {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					// 계속 전달 받음
					try {
						InputStream in = clientSource.ChatSocket.getInputStream();// 서버로부터 전달 받음
						byte[] buffer = new byte[2048];
						int length = in.read(buffer);// read 함수로 실제 입력 받는다.
						if (length == -1)
							throw new IOException();
						message = new String(buffer, 0, length, "UTF-8");
						if(message.startsWith(ClientMain.LiarClassifier))
						{
							boolean check;
							message=message.substring(1);
							if(message.startsWith("true$"))
							{
								message=message.substring(5);
								Platform.runLater(() -> {
									chatArea.appendText(message + "\n");
								});
								check=true;
							}else {
								message=message.substring(6);
								Platform.runLater(() -> {
									chatArea.appendText(message + "\n");
								});
								check=false;
							}
							String[] temp=message.split("'");
							String liar=temp[1];
							if(liar.equals(clientSource.nickname)) {
								if(check==true) {
									Platform.runLater(() -> {
										chatArea.appendText("\n축하합니다. 라이어 당신의 승리입니다!!\n");
									});
								}
								else {
									Platform.runLater(() -> {
										chatArea.appendText("\n시민들에게 패배하셨습니다.T.T\n");
									});
								}
							}else 
							{
								if(check==true) {
									Platform.runLater(() -> {
										chatArea.appendText("\n라이어에게 패배하셨습니다 T.T\n");
									});
								}else {
									Platform.runLater(() -> {
										chatArea.appendText("\n축하합니다. 시민들의 승리입니다!!\n");
									});
								}
							}
							Platform.runLater(() -> {
								game.setDisable(false);// 버튼에 쓰인 정보 바꿔주기
							});
							Platform.runLater(() -> {
								game.setText("게임시작");// 버튼에 쓰인 정보 바꿔주기
							});
							Platform.runLater(() -> {
								voteStart.setText("투표시작");// 버튼에 쓰인 정보 바꿔주기
							});
							ClientMain.serverNow=true;
						}
						else if (message.charAt(1) == ':' && message.charAt(2) == ':')// 열명 이하 여야 함
						{
							char mess = message.charAt(0);
							System.out.println(mess);
							Platform.runLater(() -> {
								userCount.setText(Character.toString(mess));// 이거 하나만 되는지?
							});
							String a = message.substring(3);
							System.out.println(a);
							vectorName = "";
							int c = 0;
							for (int i = 0; i < a.length(); i++) {
								if (a.charAt(i) == '.') {
									c = i;
									vectorName = a.substring(i + 1);
									break;
								}
							}
							message = a.substring(0, c + 1);
							System.out.println(message);
							Platform.runLater(() -> {
								chatArea.appendText(message + "\n");
							});
							vec = "";// 초기화
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									listview.getItems().clear();
								}
							});

							ArrayList<String> arr = new ArrayList<String>();
							for (int i = 0; i < vectorName.length() - 1; i++) {
								if (vectorName.charAt(i) == ' ')
									continue;
								if (vectorName.charAt(i) != ' ' && vectorName.charAt(i + 1) != ' ') {
									vec += vectorName.charAt(i);
								} else if (vectorName.charAt(i) != ' ' && vectorName.charAt(i + 1) == ' ') {
									vec += vectorName.charAt(i);
									
									System.out.println(vec);
									arr.add(vec);
									vec = "";
								}
							}
							members = FXCollections.observableArrayList(arr);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									for(int i=0; i<arr.size();i++) {
										if(arr.get(i).equals(clientSource.nickname)) {
											listview.getItems().add(arr.get(i)+" (나)");
										}else {
											listview.getItems().add(arr.get(i));
										}
									}
								}
							});
						} else if (message.equals(ClientMain.GameStartReturnClassifier)) {
	
							Platform.runLater(() -> {
								voteStart.setDisable(false);
							});
							Platform.runLater(() -> {
								game.setText("게임종료");// 버튼에 쓰인 정보 바꿔주기
							});
							Platform.runLater(() -> {
								game.setDisable(true);
							});

						} else if (message.equals("게임종료")) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							Platform.runLater(() -> {
								chatArea.appendText(message + "\n");
								game.setText("게임시작");// 버튼에 쓰인 정보 바꿔주기
							});
						} else {
							Platform.runLater(() -> {
								chatArea.appendText(message + "\n");
							});
						}

					} catch (Exception e) {
						e.printStackTrace();
						clientSource.stopClient();
						break;
					}
				}

			}
		};
		thread.start();
	}
	public void rulePopPage() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("rule.fxml"));
		Parent root;
		try {
			root = (Parent) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("rule.css").toString());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Rule of LiarGame"); 
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void voteConfirm() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("vote.fxml"));
		Parent root;
		try {
			root = (Parent) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("vote.css").toString());
			VoteController pop = loader.getController();
			pop.initFunc(members,clientSource);

			Stage stage = new Stage();
			
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setTitle("Voting Liar");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Platform.runLater(() -> {
			voteStart.setDisable(true);// 버튼에 쓰인 정보 바꿔주기
		});
		Platform.runLater(() -> {
			voteStart.setText("투표완료");// 버튼에 쓰인 정보 바꿔주기
		});
	}

	private void exitPressed(ActionEvent event) {
		closeStage();
	}

	public void initData(ClientMain loginSource, String name) {
		clientSource = loginSource;
		nickname = name;
		receive();
	}
}
