import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_background/flutter_background.dart';
import 'package:network_info_plus/network_info_plus.dart';
import 'dart:convert';

import 'package:shelf_router/shelf_router.dart' as shelf_router;
import 'package:shelf/shelf_io.dart' as shelf_io;
import 'package:shelf/shelf.dart';
import 'package:volume_controller/volume_controller.dart';

var router = shelf_router.Router();
var audio_player = AudioPlayer(playerId: "play the mother fucker");

play_the_audio() async {
  VolumeController().setVolume(0.8);
  await audio_player.play(AssetSource("yingshaoxo_gmail.mp3"), volume: 1.0);
}

start_api_service() async {
  router.get('/', (Request request) async {
    return Response.ok('remote audio player version 0.1');
  });

  router.get('/version', (Request request) async {
    return Response.ok('remote audio player version 0.1');
  });

  router.get('/play', (Request request) async {
    try {
      await play_the_audio();
      return Response.ok('ok');
    } on Exception catch (_) {
      return Response.ok("unknow error");
    }
  });

  router.post('/play_post', (Request request) async {
    try {
      // final payload = await request.readAsString();
      // Map<String, dynamic> obj = json.decode(payload);
      // final base64_audio_data = obj['base64_audio_data'];

      await play_the_audio();

      return Response.ok('ok');
    } on Exception catch (_) {
      return Response.ok("unknow error");
    }
  });

  var service = await shelf_io.serve(router, '0.0.0.0', 1919);
  print("Service is running at: ${service.address}");
}

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Remote Audio Player',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Remote Audio Player'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String service_url = "";

  @override
  initState() {
    super.initState();
    () async {
      final androidConfig = FlutterBackgroundAndroidConfig(
        notificationTitle: "remote audio player",
        notificationText: "service is running...",
        notificationImportance: AndroidNotificationImportance.Default,
      );
      await FlutterBackground.initialize(androidConfig: androidConfig);
      await FlutterBackground.enableBackgroundExecution();

      await start_api_service();

      final info = NetworkInfo();
      final host_ip = (await info.getWifiIP()) ?? ""; // 192.168.1.43
      service_url = "http://${host_ip}:1919/";

      setState(() {});
    }();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text("Current service API address:"),
            InkWell(
              child: Text(service_url),
              onTap: () async {
                await Clipboard.setData(ClipboardData(text: service_url));
              },
            ),
            SizedBox(
              height: 40,
            ),
            Text("Play the audio:"),
            InkWell(
              child: Text("${service_url}play/"),
              onTap: () async {
                await Clipboard.setData(
                    ClipboardData(text: "${service_url}play/"));
              },
            ),
          ],
        ),
      ),
    );
  }
}
