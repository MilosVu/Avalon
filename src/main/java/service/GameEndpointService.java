package service;

import domain.Command;
import domain.Room;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.HashMap;

public interface GameEndpointService {

//    vraca: svim player ima se salje spisak igraca
//    koji idu na misiju
    String Nominate(Room room, Command command);

//    vraca: da li su igraci saglasni da misija ide (Yes)
//    ili nisu (No)
    String Vote(Room room, Command command);

//    vraca: da li je misija prosla (Yes)
//    ili pala (No)
    String MissionVote(Room room, Command command);

//    vraca: da li je pogodio assasin ko je merlin
//    Yes/No
    String AssasinKill(Room room, Command command);
    //Dodeljuje igracima uloge !
    void setPlayersRoll(Room room) throws IOException, EncodeException;
    void setOnMove(Room room, HashMap<Integer,String> hashMap);
    void sendPlayersWhoIsOnMove(Room room,HashMap <Integer,String> hashMap,int onCurrentMove) throws IOException, EncodeException;


}
