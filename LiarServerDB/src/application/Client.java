package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.biz.MyLiarBiz;
import com.vo.MyLiarVo;


public class Client {

	public Socket LoginSocket;// 9876
	public Socket ChatSocket;// 9877

	private static int totalNum = 0;
	private static Vector<String> names = new Vector<String>();
	private static Vector<Integer> vec=new Vector<Integer>();
	private static String liarName;
	private static int gameClient=0;
	private static MyDB myDB= new MyDB();
	private static String wordTopic;
	private static Hashtable<String,Integer> mp=new Hashtable<>();
	public static final String loginClassifier="~";
	public static final String CreateAvailableClassifier="|";
	public static final String CreateNotAvailableClassifier="@";
	public static final String GameStartClassifier="#";
	public static final String LiarClassifier="$";
	public static final String GameStartReturnClassifier="%%";
	public static final String ServerNotAvailable="**";
	
	public static boolean serverAvailable=true;
	
	public Client(Socket LoginSocket, Socket ChatSocket) {
		this.LoginSocket = LoginSocket;
		this.ChatSocket = ChatSocket;
		receiveLogin();// 반복적으로 client로 부터 message 전달 받을 수 있도록 만든다.
		receiveChat();
	}

	// 클라이언트로부터 로그인 정보를 전달 받는 메소드입니다.
	public void receiveLogin() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						InputStream in = LoginSocket.getInputStream();
						byte[] buffer = new byte[512];// 버퍼 이용 한번에 512바이트 만큼
						int length = in.read(buffer);
						while (length == -1)
							throw new IOException();
						// 메세지를 정상적으로 받은 경우
						if(totalNum>=9) {
							sendLogin(ServerNotAvailable);
						}
						if(serverAvailable==false)
						{
							sendLogin(ServerNotAvailable);
						}else {
						// 메세지를 정상적으로 받은 경우
						System.out.println("[로그인 정보 수신 성공]");
						String message = new String(buffer, 0, length, "UTF-8");
						// message에 쓰고 싶은 nickName과 password가 담겨 있을 것이다.
						String id=null; String password=null;
						String[] array=message.split(loginClassifier);
						id=array[0]; password=array[1];
						
						boolean check = myDB.findByUserId(id);// 닉네임 검사 함수 info는 userConnectInfo class에서 확인 가능
						if (check == true) {// 생성 가능한 경우
							sendLogin(CreateAvailableClassifier);
							System.out.println("[ID생성 가능]");
							MyDB.addUser(id, password);
							// 채팅 소켓으로 값이 보내진다.
							++totalNum;// 서버와 연결되어 있는 + 있던 사람들의 수
							names.add(id);// 이름 목록에 추가;
							vec.add(0);
							String nameMessage = "";
							for (String s : names) {
								nameMessage += s + " ";
							}
							String chatMessage = Integer.toString(totalNum) + "::" + id + "님이 입장하셨습니다. "
									+ nameMessage;
							for (Client client : Main.clients) {
								client.sendChat(chatMessage);
							}
							int numSizeNow=totalNum;
						} else {
							sendLogin(CreateNotAvailableClassifier);
							System.out.println("[ID중복]");
						}
					}
					}
				} catch (Exception e) {
					try {
						System.out.println("[로그인 정보 수신 오류] 리시브 로그인 오류 ");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	}

	// 클라이언트에게 로그인 정보를 전송하는 메소드
	public void sendLogin(String message) {
		// Runnable library 이용해서 thread 정의 해주고
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					// OutputStream 사용 이유, 메세지를 보내주고자 할 때는 outputStream으로
					OutputStream out = LoginSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
					System.out.println("[로그인 정보 송신 성공]");
				} catch (Exception e) {
					try {
						System.out.println("[로그인 정보 송신 오류]");
						// 예외 발생 시 메인 함수의 client의 정보를 담든 clients배열에서
						// 현재 존재하는 Client를 지워준다.
						Main.clients.remove(Client.this);
						// 오류가 생긴 client의 socket을 닫는다.
						LoginSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		// Main threadPool에 추가 한다.
		Main.threadPool.submit(thread);
	}

	public void receiveChat() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						InputStream in = ChatSocket.getInputStream();//InputStream 바이트 단위라 한글 꺠짐
						byte[] buffer = new byte[2048];
						int length = in.read(buffer);
						while (length == -1)
							throw new IOException();
						System.out.println("[메세지 수신 성공]");
						String message = new String(buffer, 0, length, "UTF-8");
						if (message.equals(GameStartClassifier)) {
							serverAvailable=false;
							List<String> list=liarSelect();
							for(String a:list) {
								System.out.println(a);
								mp.put(a,0);
							}
						}
						else if (message.length()>=9 &&message.substring(0,8).equals(":종료할겁니다.")) {
							message=message.substring(8);
							MyDB.deleteUser(message);
							totalNum--;
							Main.clients.remove(Client.this);
							for(int i=0; i<names.size();i++) {
								System.out.println(names.get(i));
							}
							for(int i=0; i<names.size();i++) {
								if(names.get(i).equals(message)) {
									names.remove(i);
									vec.remove(i);
									break;
								}
							}
							for(int i=0; i<names.size();i++) {
								System.out.println(names.get(i));
							}
							String nameMessage="";
							for(String s:names)
							{
								nameMessage+=s+" ";
							}
							String chatMessage=Integer.toString(totalNum)+"::"+message+"님이 퇴장하셨습니다. "+nameMessage;
							for(Client client: Main.clients) {
								client.sendChat(chatMessage);
							}
						} 
						else if(message.length()>=4 && message.substring(0,3).equals("투표:")){
							for(Client client: Main.clients) {
								client.sendChat("누군가 투표했습니다. \n다시 투표는 불가능하며 모두가 투표하면 결과가 공개됩니다.");
							}
							try {
								TimeUnit.MILLISECONDS.sleep(100);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							String guessLiar=message.substring(3);//투표한 결과 값 반영
							int now=mp.get(guessLiar);
							mp.put(guessLiar,now+1);//계산을 해준다.
							gameClient--;
							if(gameClient==0)
							{
								boolean LiarWin=true;
								ArrayList<CheckLiar> m= new ArrayList<>();
								mp.forEach((key,value)->{
									System.out.println(key);
									System.out.println(value);
									CheckLiar checkLiar=new CheckLiar(key,value);
									m.add(checkLiar);
								});

								Collections.sort(m,Collections.reverseOrder());
								
								
								for(CheckLiar chk: m) {
									System.out.println(chk.userID+chk.count);
								}
								if(m.size()>1) {
									if(((CheckLiar)m.get(0)).count>((CheckLiar)m.get(1)).count) {
										if(((CheckLiar)m.get(0)).userID.equals(liarName))
										{
											LiarWin=false;//라이어가 가장 많이 투표 받은 경우
										}
									}
								}
							
								System.out.println(Main.clients.size());

								for(Client client: Main.clients) {

									client.sendChat(LiarClassifier+Boolean.toString(LiarWin)+"$"+"라이어는 <'"+liarName+"'> 님이었습니다.");
								}
								serverAvailable=true;
							}
							
						}
						else {
							// 전달 받은 메세지를 다른 client에게도 보낼 수 있도록
							for (Client client : Main.clients) {
								client.sendChat(message);
							}
						}

					}
				} catch (Exception e) {
					try {
						System.out.println("[메세지 수신 오류] ");
						e.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	}

	// 클라이언트에게 메세지를 전송하는 메소드
	public void sendChat(String message) {
		// Runnable library 이용해서 thread 정의 해주고
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					// OutputStream 사용 이유, 메세지를 보내주고자 할 때는 outputStream으로
					OutputStream out = ChatSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					// 오류 미 발생 시 서버에서 client로 전송하기 위해서
					// out에서 write 해준다.
					out.write(buffer);
					System.out.println("[메세지 송신 성공]");
					// 성공적으로 여기까지 전송했다는 것을 알리기 위해 반드시 flush 해주어야 한다.
					out.flush();
				} catch (Exception e) {
					try {
						System.out.println("[메세지 송신 오류]");
						// 예외 발생 시 메인 함수의 client의 정보를 담든 clients배열에서
						// 현재 존재하는 Client를 지워준다.
						Main.clients.remove(Client.this);
						// 오류가 생긴 client의 socket을 닫는다.
						ChatSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		// Main threadPool에 추가 한다.
		Main.threadPool.submit(thread);
	}

	public List<String> liarSelect() {
		
		MyLiarBiz biz=new MyLiarBiz();
		List<MyLiarVo> all=biz.select_all_liar();
		List<String> list=new ArrayList<String>();
		for(MyLiarVo vo:all) {
			list.add(vo.getUserID());
		}
		gameClient=all.size();// 현재 접속해 있는 client 수
		Random rand = new Random();
		int liarIndex = rand.nextInt(gameClient);// rand.nextInt()반환 값 0~n미만의 정수
		int clientCnt = 0;
		liarName=all.get(liarIndex).getUserID();
		
		//여기 밑에 수정해야 한다.
		String word=wordProcess();
		Iterator<Client> iterator = Main.clients.iterator();// Iterator 이용 반복
		while (iterator.hasNext()) {
			// 하나씩 모든 client 에 접근한다.
			Client client = iterator.next();
			client.sendChat(GameStartReturnClassifier);
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			client.sendChat("\n게임이 곧 시작됩니다.\n");
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			client.sendChat("[게임 시작]\n게임 주제는 " + wordTopic + " 입니다\n");
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (liarName.equals(Client.names.get(clientCnt))) {// liar 인 경우
				client.sendChat("본인은 [라이어]입니다. 걸리지 않게 잘 행동하세요\n");
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("[라이어가 선정되었습니다]");
			} else {
				client.sendChat("본인은 [시민]입니다.\n");
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				client.sendChat("주제어는 [" + word + "] 입니다.\n");
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				};

			}
			clientCnt++;
		}
		return list;
	}

	public String wordProcess() {
		// 주제 "음식, 동물, 직업" 단어로 랜덤 선택
		ArrayList<String> topic = new ArrayList<>(); // 문자열 타입의 주제 객체 생성
		// add
		String giveTopic = "";
		topic.add("음식");
		topic.add("동물");
		topic.add("직업");

		Collections.shuffle(topic); // shuffle을 통해 주제 값을 랜덤으로 순서 재배치
		String getTopic = topic.get(0); // mix된 주제중 첫번째 주제 가져오기
		wordTopic=getTopic;
		for (Client client : Main.clients) {

			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// 선택 된 주제의 제시어파일 불러오기
		try {
			File fileName;
			if (getTopic == "음식") {
				fileName = new File("./Topic/food.txt"); // Path = 주제가 음식일때 음식 제시어파일
			} else if (getTopic == "동물") {
				fileName = new File("./Topic/animal.txt"); // Path = 주제가 동물일때 동물 제시어파일
			} else {// 직업
				fileName = new File("./Topic/job.txt"); // Path = 주제가 직업일때 직업 제시어파일
			}
			FileReader filereader = new FileReader(fileName);
			// 입력 버퍼 생성
			BufferedReader bufReader = new BufferedReader(filereader);
			String line = "";
			ArrayList<String> getWord = new ArrayList<String>();
			while ((line = bufReader.readLine()) != null) {
				getWord.add(line);
			}
			// .readLine()은 끝에 개행문자를 읽지 않는다.

			// 제시어 랜덤으로 섞기
			Collections.shuffle(getWord); // 섞기
			giveTopic = getWord.get(0); // 섞은 제시어 첫번째 뽑기

			bufReader.close();
			filereader.close();

		} catch (FileNotFoundException e) {
			e.getStackTrace();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return giveTopic; // 제시어 반환
	}
}
class CheckLiar implements Comparable<CheckLiar>{
	public String userID;
	public int count;
	public CheckLiar(String userID, int count) {
		super();
		this.userID = userID;
		this.count = count;
	}
	@Override
	public int compareTo(CheckLiar check) {
		if (check.count < count) {
		return 1;
		} else if (check.count > count) {
		return -1;
		}
	return 0;
	
	}
}