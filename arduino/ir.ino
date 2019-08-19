#include <IRremote.h>

int RECV_PIN = 2;
IRrecv irrecv(RECV_PIN);
IRsend irsend;

decode_results results;

long hstol(String recv) {
  char c[recv.length() + 1];
  recv.toCharArray(c, recv.length() + 1);
  return strtol(c, NULL, 16);
}

void readIr(String str) {
  if (irrecv.decode(&results)) {
    str.trim();
    Serial.print("saved:" + str + "-");
    Serial.println(results.value, HEX);
    irrecv.resume();
  } else {
    Serial.println("No Signal - No capture signal found");
  }
}

void sendIr(String str) {
  irsend.sendNEC(hstol(str), 32);
  irrecv.enableIRIn();
  Serial.println("Command Sent");
  delay(100);
}

void setup()
{
  Serial.begin(9600);
  irrecv.enableIRIn();
}

void loop() {
  if (Serial.available() > 0) {
    String incoming = Serial.readString();
    if (incoming.startsWith("read:")) {
      readIr(incoming.substring(5));  // read:
    } else if (incoming.startsWith("send:")) {
      sendIr(incoming.substring(5)); // send:
    }
    Serial.flush();
  }
}
