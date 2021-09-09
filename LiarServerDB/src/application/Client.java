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
		receiveLogin();// �ݺ������� client�� ���� message ���� ���� �� �ֵ��� �����.
		receiveChat();
	}

	// Ŭ���̾�Ʈ�κ��� �α��� ������ ���� �޴� �޼ҵ��Դϴ�.
	public void receiveLogin() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						InputStream in = LoginSocket.getInputStream();
						byte[] buffer = new byte[512];// ���� �̿� �ѹ��� 512����Ʈ ��ŭ
						int length = in.read(buffer);
						while (length == -1)
							throw new IOException();
						// �޼����� ���������� ���� ���
						if(totalNum>=9) {
							sendLogin(ServerNotAvailable);
						}
						if(serverAvailable==false)
						{
							sendLogin(ServerNotAvailable);
						}else {
						// �޼����� ���������� ���� ���
						System.out.println("[�α��� ���� ���� ����]");
						String message = new String(buffer, 0, length, "UTF-8");
						// message�� ���� ���� nickName�� password�� ��� ���� ���̴�.
						String id=null; String password=null;
						String[] array=message.split(loginClassifier);
						id=array[0]; password=array[1];
						
						boolean check = myDB.findByUserId(id);// �г��� �˻� �Լ� info�� userConnectInfo class���� Ȯ�� ����
						if (check == true) {// ���� ������ ���
							sendLogin(CreateAvailableClassifier);
							System.out.println("[ID���� ����]");
							MyDB.addUser(id, password);
							// ä�� �������� ���� ��������.
							++totalNum;// ������ ����Ǿ� �ִ� + �ִ� ������� ��
							names.add(id);// �̸� ��Ͽ� �߰�;
							vec.add(0);
							String nameMessage = "";
							for (String s : names) {
								nameMessage += s + " ";
							}
							String chatMessage = Integer.toString(totalNum) + "::" + id + "���� �����ϼ̽��ϴ�. "
									+ nameMessage;
							for (Client client : Main.clients) {
								client.sendChat(chatMessage);
							}
							int numSizeNow=totalNum;
						} else {
							sendLogin(CreateNotAvailableClassifier);
							System.out.println("[ID�ߺ�]");
						}
					}
					}
				} catch (Exception e) {
					try {
						System.out.println("[�α��� ���� ���� ����] ���ú� �α��� ���� ");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	}

	// Ŭ���̾�Ʈ���� �α��� ������ �����ϴ� �޼ҵ�
	public void sendLogin(String message) {
		// Runnable library �̿��ؼ� thread ���� ���ְ�
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					// OutputStream ��� ����, �޼����� �����ְ��� �� ���� outputStream����
					OutputStream out = LoginSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
					System.out.println("[�α��� ���� �۽� ����]");
				} catch (Exception e) {
					try {
						System.out.println("[�α��� ���� �۽� ����]");
						// ���� �߻� �� ���� �Լ��� client�� ������ ��� clients�迭����
						// ���� �����ϴ� Client�� �����ش�.
						Main.clients.remove(Client.this);
						// ������ ���� client�� socket�� �ݴ´�.
						LoginSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		// Main threadPool�� �߰� �Ѵ�.
		Main.threadPool.submit(thread);
	}

	public void receiveChat() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						InputStream in = ChatSocket.getInputStream();//InputStream ����Ʈ ������ �ѱ� ����
						byte[] buffer = new byte[2048];
						int length = in.read(buffer);
						while (length == -1)
							throw new IOException();
						System.out.println("[�޼��� ���� ����]");
						String message = new String(buffer, 0, length, "UTF-8");
						if (message.equals(GameStartClassifier)) {
							serverAvailable=false;
							List<String> list=liarSelect();
							for(String a:list) {
								System.out.println(a);
								mp.put(a,0);
							}
						}
						else if (message.length()>=9 &&message.substring(0,8).equals(":�����Ұ̴ϴ�.")) {
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
							String chatMessage=Integer.toString(totalNum)+"::"+message+"���� �����ϼ̽��ϴ�. "+nameMessage;
							for(Client client: Main.clients) {
								client.sendChat(chatMessage);
							}
						} 
						else if(message.length()>=4 && message.substring(0,3).equals("��ǥ:")){
							for(Client client: Main.clients) {
								client.sendChat("������ ��ǥ�߽��ϴ�. \n�ٽ� ��ǥ�� �Ұ����ϸ� ��ΰ� ��ǥ�ϸ� ����� �����˴ϴ�.");
							}
							try {
								TimeUnit.MILLISECONDS.sleep(100);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							String guessLiar=message.substring(3);//��ǥ�� ��� �� �ݿ�
							int now=mp.get(guessLiar);
							mp.put(guessLiar,now+1);//����� ���ش�.
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
											LiarWin=false;//���̾ ���� ���� ��ǥ ���� ���
										}
									}
								}
							
								System.out.println(Main.clients.size());

								for(Client client: Main.clients) {

									client.sendChat(LiarClassifier+Boolean.toString(LiarWin)+"$"+"���̾�� <'"+liarName+"'> ���̾����ϴ�.");
								}
								serverAvailable=true;
							}
							
						}
						else {
							// ���� ���� �޼����� �ٸ� client���Ե� ���� �� �ֵ���
							for (Client client : Main.clients) {
								client.sendChat(message);
							}
						}

					}
				} catch (Exception e) {
					try {
						System.out.println("[�޼��� ���� ����] ");
						e.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	}

	// Ŭ���̾�Ʈ���� �޼����� �����ϴ� �޼ҵ�
	public void sendChat(String message) {
		// Runnable library �̿��ؼ� thread ���� ���ְ�
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					// OutputStream ��� ����, �޼����� �����ְ��� �� ���� outputStream����
					OutputStream out = ChatSocket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					// ���� �� �߻� �� �������� client�� �����ϱ� ���ؼ�
					// out���� write ���ش�.
					out.write(buffer);
					System.out.println("[�޼��� �۽� ����]");
					// ���������� ������� �����ߴٴ� ���� �˸��� ���� �ݵ�� flush ���־�� �Ѵ�.
					out.flush();
				} catch (Exception e) {
					try {
						System.out.println("[�޼��� �۽� ����]");
						// ���� �߻� �� ���� �Լ��� client�� ������ ��� clients�迭����
						// ���� �����ϴ� Client�� �����ش�.
						Main.clients.remove(Client.this);
						// ������ ���� client�� socket�� �ݴ´�.
						ChatSocket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		// Main threadPool�� �߰� �Ѵ�.
		Main.threadPool.submit(thread);
	}

	public List<String> liarSelect() {
		
		MyLiarBiz biz=new MyLiarBiz();
		List<MyLiarVo> all=biz.select_all_liar();
		List<String> list=new ArrayList<String>();
		for(MyLiarVo vo:all) {
			list.add(vo.getUserID());
		}
		gameClient=all.size();// ���� ������ �ִ� client ��
		Random rand = new Random();
		int liarIndex = rand.nextInt(gameClient);// rand.nextInt()��ȯ �� 0~n�̸��� ����
		int clientCnt = 0;
		liarName=all.get(liarIndex).getUserID();
		
		//���� �ؿ� �����ؾ� �Ѵ�.
		String word=wordProcess();
		Iterator<Client> iterator = Main.clients.iterator();// Iterator �̿� �ݺ�
		while (iterator.hasNext()) {
			// �ϳ��� ��� client �� �����Ѵ�.
			Client client = iterator.next();
			client.sendChat(GameStartReturnClassifier);
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			client.sendChat("\n������ �� ���۵˴ϴ�.\n");
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			client.sendChat("[���� ����]\n���� ������ " + wordTopic + " �Դϴ�\n");
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			if (liarName.equals(Client.names.get(clientCnt))) {// liar �� ���
				client.sendChat("������ [���̾�]�Դϴ�. �ɸ��� �ʰ� �� �ൿ�ϼ���\n");
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("[���̾ �����Ǿ����ϴ�]");
			} else {
				client.sendChat("������ [�ù�]�Դϴ�.\n");
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				client.sendChat("������� [" + word + "] �Դϴ�.\n");
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
		// ���� "����, ����, ����" �ܾ�� ���� ����
		ArrayList<String> topic = new ArrayList<>(); // ���ڿ� Ÿ���� ���� ��ü ����
		// add
		String giveTopic = "";
		topic.add("����");
		topic.add("����");
		topic.add("����");

		Collections.shuffle(topic); // shuffle�� ���� ���� ���� �������� ���� ���ġ
		String getTopic = topic.get(0); // mix�� ������ ù��° ���� ��������
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
		// ���� �� ������ ���þ����� �ҷ�����
		try {
			File fileName;
			if (getTopic == "����") {
				fileName = new File("./Topic/food.txt"); // Path = ������ �����϶� ���� ���þ�����
			} else if (getTopic == "����") {
				fileName = new File("./Topic/animal.txt"); // Path = ������ �����϶� ���� ���þ�����
			} else {// ����
				fileName = new File("./Topic/job.txt"); // Path = ������ �����϶� ���� ���þ�����
			}
			FileReader filereader = new FileReader(fileName);
			// �Է� ���� ����
			BufferedReader bufReader = new BufferedReader(filereader);
			String line = "";
			ArrayList<String> getWord = new ArrayList<String>();
			while ((line = bufReader.readLine()) != null) {
				getWord.add(line);
			}
			// .readLine()�� ���� ���๮�ڸ� ���� �ʴ´�.

			// ���þ� �������� ����
			Collections.shuffle(getWord); // ����
			giveTopic = getWord.get(0); // ���� ���þ� ù��° �̱�

			bufReader.close();
			filereader.close();

		} catch (FileNotFoundException e) {
			e.getStackTrace();
		} catch (IOException e) {
			e.getStackTrace();
		}
		return giveTopic; // ���þ� ��ȯ
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