import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:album_picker/album_picker.dart';
import 'package:image_picker/image_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _filePath;
  String _fileInputPath = "";
  String _fileOutputPath = "";
  String _smallVideoPath;

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
          body: SingleChildScrollView(
            child: Column(
              children: <Widget>[
                Row(
                  children: <Widget>[
                    Text(
                      "文件路径: ",
                      style: TextStyle(fontSize: 20),
                    ),
                    Expanded(
                      child: Text(_filePath == null ? "" : _filePath),
                    )
                  ],
                ),
                Row(
                  children: <Widget>[
                    Text(
                      "Input: ",
                      style: TextStyle(fontSize: 20),
                    ),
                    Expanded(
                      child: Text(_fileInputPath),
                    )
                  ],
                ),
                Row(
                  children: <Widget>[
                    Text(
                      "Output: ",
                      style: TextStyle(fontSize: 20),
                    ),
                    Expanded(
                      child: Text(_fileOutputPath),
                    )
                  ],
                ),
                Row(
                  children: <Widget>[
                    Text(
                      "小视频路径: ",
                      style: TextStyle(fontSize: 20),
                    ),
                    Expanded(
                      child: Text(_smallVideoPath == null ? "" : _smallVideoPath),
                    )
                  ],
                ),
              ],
            ),
          ),
          floatingActionButton: Column(
            mainAxisAlignment: MainAxisAlignment.end,
            children: <Widget>[
              RaisedButton(
                  child: Icon(Icons.album),
                  onPressed: () async {
                    List<String> paths = await AlbumPicker.pickFile();
                    String str = "";
                    paths.forEach((f) {
                      str += f + "\n";
                    });
                    setState(() {
                      _filePath = str;
                    });
                  }),
              RaisedButton(
                child: Text("选择视频"),
                onPressed: () async {
                  var image = await ImagePicker.pickVideo(source: ImageSource.gallery);
                  setState(() {
                    if(image != null) {
                      _fileInputPath = image.path;
                    }
                  });
                },
              ),
              RaisedButton(
                child: Text("压缩视频"),
                onPressed: () async {
                  String destPath = await AlbumPicker.videoCompress(_fileInputPath);
                  setState(() {
                    _fileOutputPath = destPath;
                  });
                },
              ),
              RaisedButton(
                child: Text("拍摄小视频"),
                onPressed: () async {
                  String destPath = await captureSmallVideo();
                  setState(() {
                    _smallVideoPath = destPath;
                  });
                },
              )
            ],
          )),
    );
  }

  Future<String> captureSmallVideo() async{
    var image = await ImagePicker.pickVideo(source: ImageSource.camera,maxDuration: Duration(seconds: 10));
    String destPath = await AlbumPicker.videoCompress(image.path);
    return destPath;
  }
}
