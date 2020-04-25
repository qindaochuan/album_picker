import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:album_picker/album_picker.dart';
import 'package:image_picker/image_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _filePath = null;
  String _fileInputPath = "";
  String _fileOutputPath = "";

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
              ],
            ),
          ),
          floatingActionButton: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
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
                child: Text("Select File"),
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
                child: Text("Start Compress"),
                onPressed: () async {
                  String destPath = await AlbumPicker.videoCompress(_fileInputPath);
                  setState(() {
                    _fileOutputPath = destPath;
                  });
                },
              )
            ],
          )),
    );
  }
}
