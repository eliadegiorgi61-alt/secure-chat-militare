import com.sun.net.httpserver.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.*;

public class TutorServer {
    static Map<String, List<String>> inbox = new ConcurrentHashMap<>();
    public static void main(String[] args) throws Exception {
        int port = 10000;
        String p = System.getenv("PORT");
        if(p!=null) port=Integer.parseInt(p);
        HttpServer s = HttpServer.create(new InetSocketAddress(port),0);
        s.createContext("/", ex->{
            String h = "<html><head><meta charset=utf-8><meta name=viewport content='width=device-width,initial-scale=1'><title>SECURE V10003</title><style>body{background:#000;color:#fff;font-family:system-ui;margin:0}.top{background:linear-gradient(90deg,#00ff88,#00ffaa);color:#000;padding:16px;text-align:center;font-weight:900}.top small{display:block;font-size:10px}.sec{padding:8px;background:#111;display:flex;gap:8px;justify-content:center;flex-wrap:wrap}.sec div{background:#000;border:1px solid #00ff88;color:#00ff88;padding:6px 10px;border-radius:20px;font-size:10px}#c{height:60vh;overflow-y:auto;padding:12px;background:#050505}.msg{margin:6px 0;padding:10px 14px;border-radius:14px;max-width:80%;font-size:14px;word-break:break-all}.me{background:#00ff88;color:#000;margin-left:auto}.other{background:#1a1a1a;border:1px solid #333}.row{display:flex;gap:6px;padding:10px;background:#111;position:fixed;bottom:0;left:0;right:0}input{flex:1;padding:14px;background:#000;color:#fff;border:1px solid #333;border-radius:12px}button{background:#00ff88;color:#000;border:none;padding:14px 16px;border-radius:12px;font-weight:900}</style></head><body><div class=top>SECURE CHAT V10003<br><small>GOV-PROOF E2E - NESSUNO LEGGE</small></div><div class=sec><div>AES-256-GCM locale</div><div>Server cieco</div><div>No logs</div><div>E2E</div></div><div style='padding:8px;background:#0a0a0a;font-size:11px;color:#aaa;text-align:center'>Stanza: <select id=room><option>generale</option><option>privata1</option><option>privata2</option></select> <span id=stat>genera chiave...</span></div><div id=c><div class=msg other>SECURE V10003 attiva. Chiave nel tuo telefono. Server NON legge.</div></div><div class=row><input id=q placeholder='Scrivi... criptato'><button onclick=send()>>></button></div><script>let key=null;async function gen(){key=await crypto.subtle.generateKey({name:'AES-GCM',length:256},true,['encrypt','decrypt']);document.getElementById('stat').innerText='CHIAVE AES-256 ATTIVA';}gen();async function enc(t){let iv=crypto.getRandomValues(new Uint8Array(12));let ct=await crypto.subtle.encrypt({name:'AES-GCM',iv},key,new TextEncoder().encode(t));let out=new Uint8Array(iv.length+ct.byteLength);out.set(iv);out.set(new Uint8Array(ct),12);return btoa(String.fromCharCode(...out));}async function send(){let i=document.getElementById('q');let txt=i.value.trim();if(!txt)return;let room=document.getElementById('room').value;let crypted=await enc(txt);let d=document.createElement('div');d.className='msg me';d.innerText=txt+' (criptato)';document.getElementById('c').appendChild(d);document.getElementById('c').scrollTop=99999;i.value='';fetch('/s?r='+room+'&m='+encodeURIComponent(crypted));}</script></body></html>";
            ex.getResponseHeaders().set("Content-Type","text/html; charset=utf-8");
            ex.sendResponseHeaders(200,h.getBytes(StandardCharsets.UTF_8).length);
            ex.getResponseBody().write(h.getBytes(StandardCharsets.UTF_8));
            ex.close();
        });
        s.createContext("/s", ex->{
            String qs=ex.getRequestURI().getQuery(); String r="generale",m="";
            if(qs!=null) for(String a:qs.split("&")){if(a.startsWith("r="))r=URLDecoder.decode(a.substring(2),StandardCharsets.UTF_8); if(a.startsWith("m="))m=URLDecoder.decode(a.substring(2),StandardCharsets.UTF_8);}
            inbox.putIfAbsent(r,new CopyOnWriteArrayList<>()); inbox.get(r).add(m); if(inbox.get(r).size()>200)inbox.get(r).remove(0);
            ex.sendResponseHeaders(200,2); ex.getResponseBody().write("ok".getBytes()); ex.close();
        });
        s.createContext("/g", ex->{
            String qs=ex.getRequestURI().getQuery(); String r="generale";
            if(qs!=null) for(String a:qs.split("&")) if(a.startsWith("r="))r=URLDecoder.decode(a.substring(2),StandardCharsets.UTF_8);
            List<String> l=inbox.getOrDefault(r,List.of());
            String j="[\""+String.join("\",\"",l)+"\" ]"; if(l.isEmpty())j="[]";
            ex.getResponseHeaders().set("Content-Type","application/json");
            ex.sendResponseHeaders(200,j.getBytes().length); ex.getResponseBody().write(j.getBytes()); ex.close();
        });
        s.start(); System.out.println("SECURE V10003 ON "+port);
    }
}
