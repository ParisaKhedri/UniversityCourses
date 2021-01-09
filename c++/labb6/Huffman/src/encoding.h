/*
 * TDDD86 Huffman Encoding
 * This file declares the functions that you will need to write in this
 * assignment for your Huffman Encoder in huffmanencoding.cpp.
 *
 * Please do not modify this provided file. Your turned-in files should work
 * with an unmodified version of all provided code files.
 */

#ifndef _encoding_h
#define _encoding_h

#include <iostream>
#include <string>
#include <map>
#include "bitstream.h"
#include "HuffmanNode.h"
#include <queue>
using namespace std;

/*
 * See huffmanencoding.cpp for documentation of these functions
 * (which you are supposed to write, based on the spec).
 */
/*
 *
*/

#include "encoding.h"
#include <iostream>
#include <string>
using namespace std;


map<int, int> buildFrequencyTable(istream& input);
HuffmanNode* buildEncodingTree(const map<int, int> &freqTable);
void buildMap(HuffmanNode* encodingTree, map<int,string>& encodingMap, string binaryCode);
map<int, string> buildEncodingMap(HuffmanNode* encodingTree);
void encodeData(istream& input, const map<int, string> &encodingMap, obitstream& output);
void decodeData(ibitstream& input, HuffmanNode* encodingTree, ostream& output);
string mapToStr(map<int, int> map);
void compress(istream& input, obitstream& output);
void decompress(ibitstream& input, ostream& output);
void freeTree(HuffmanNode* node);

#endif
