package com.hurray.landlord.server.stub;

import com.hurray.landlord.server.stub.MessageHolder;

interface IMessageListener{
   void onReceive(in MessageHolder holder);

   void onSentResult(in boolean status, in MessageHolder holder);

}