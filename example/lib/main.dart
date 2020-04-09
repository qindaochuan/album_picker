import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:album_picker/album_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _filePath = null;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('相册混合显示图片视频'),
        ),
        body: Column(
          children: <Widget>[
            Center(
              child: Text('文件路径: ${_filePath}'),
            ),
          ],
        ),
        floatingActionButton: RaisedButton(
          child: Icon(Icons.album),
          onPressed: () async{
            List<String> paths = await AlbumPicker.pickFile();
            String str = "";
            paths.forEach((f){
              str += f + ";";
            });
            setState(() {
              _filePath = str;
            });
          }
        ),
      ),
    );
  }
}
