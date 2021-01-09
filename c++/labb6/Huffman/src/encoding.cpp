/*
 *HuffmanKodning labb6
*/

#include "encoding.h"
#include <iostream>
#include <string>
using namespace std;

/*
 * läser in värdet ifrån input och lägger till den som ett par av elementet och antal förekomster i en map
*/
map<int, int> buildFrequencyTable(istream& input) {
    map<int, int> freqTable;
    int i = input.get();
    while (i != -1) {
        freqTable[i]++;
        i = input.get();
    }
    freqTable[PSEUDO_EOF] = 1;

    return freqTable;
}

/*
 * skapar ett träd genom att skapa nya node med noder
 * i freqtable som zero och one
*/
HuffmanNode* buildEncodingTree(const map<int, int> &freqTable) {
    // comprator som använder sig av operator < från Huffmannode
    auto  Compare = [](HuffmanNode *n1, HuffmanNode *n2){
        return *n1 < *n2;
    };

    priority_queue <HuffmanNode*, vector<HuffmanNode*>, decltype(Compare)> tree(Compare);
    // lägger till alla element i freqtable i trädet men nullptr som zero och one
    for (auto i : freqTable){
        HuffmanNode* node = new HuffmanNode(i.first, i.second, nullptr, nullptr);
        tree.push(node);
    }

    while (tree.size() > 1){ //
        HuffmanNode*  n1 = tree.top();
        tree.pop();
        HuffmanNode*  n2 = tree.top();
        tree.pop();
        tree.push(new HuffmanNode (NOT_A_CHAR, n1->count + n2->count, n1, n2));
    }


    return tree.top(); // root node

}

/*
 * skapar en map som inehåller character och binärkod beroende på
 * om den ör en leftnode eller en rightnode
*/
void buildMap(HuffmanNode* encodingTree, map<int,string>& encodingMap, string binaryCode){
    if(encodingTree == nullptr){
        return;
    }else{
        if (encodingTree->character != NOT_A_CHAR){ // är en riktigt char som vi ska hitta kod för
            encodingMap[encodingTree->character] = binaryCode;
        }
        buildMap(encodingTree->zero, encodingMap, binaryCode + "0");
        buildMap(encodingTree->one, encodingMap, binaryCode + "1");
    }

}


map<int, string> buildEncodingMap(HuffmanNode* encodingTree) {
    map<int, string> encodingMap;

    buildMap(encodingTree, encodingMap, "");

    return encodingMap;
}

/*
 * läser in värde från input och encodear den mha encodingmap
*/
void encodeData(istream& input, const map<int, string> &encodingMap, obitstream& output) {
    int i = input.get();
    while (i != -1) {
        for(auto code: encodingMap.at(i)){
            output.writeBit(code-48); // char to int
        }
        i = input.get();
    }

    for(auto code: encodingMap.at(PSEUDO_EOF)){
        output.writeBit(code-48);
    }

}

/*
 *läser värde frång input och decodear den mhv encodingtree
*/
void decodeData(ibitstream& input, HuffmanNode* encodingTree, ostream& output){
    HuffmanNode* current = encodingTree;
    int i = input.readBit();
    while (i != -1) {
        if(i == 1){
            current = current->one;
        }else{
            current = current->zero;
        }
        if(current->isLeaf()){
            if (current->character == PSEUDO_EOF){
                break;
            }
            output.put(current->character); // skiver character till output
            current = encodingTree; // sätt tillbaka för att konna decoda andra

        }
        i = input.readBit();
    }

}


/*
 * tar in en map och returnerar den som en sträng
*/
string mapToStr(map<int, int> map){
    string res;

    res += '{';

    for(pair<int,int> pair: map){
        res += to_string(pair.first);
        res += ':';
        res += to_string(pair.second);
        res += ',';
        res += ' ';
    }
    res = res.substr(0, res.size()-2); // skapar en substr utav res utan de sista onödiga ',' och ' '
    res += '}';
    return res;
}


/*
 * skapar en header mhv mapToStr och skriver den till output
 * kallar på encode data för att skriva encodead data till output
*/
void compress(istream& input, obitstream& output){
    map<int,int> freqTable = buildFrequencyTable(input);

    string sFreqTable = mapToStr(freqTable); // header
    for(char c: sFreqTable){
        output.put(c);
    }

    input.clear();  // seek början av input för att kunna anvönda den när vi kallar på encodeData
    input.seekg(0, ios::beg);

    HuffmanNode* encodingTree = buildEncodingTree(freqTable);
    map<int, string> encodingMap = buildEncodingMap(encodingTree);
    encodeData(input, encodingMap, output);
    freeTree(encodingTree); // fär att vi skapar en encodingtree

}


/*
 * skapar en freqTable genom att läsa av headerfilen
 * kallar på decodeData med encodingtree som har skapats mha feqTable
*/
void decompress(ibitstream& input, ostream& output){
    map<int, int> freqTable;
    input.get(); // vill inte ha med "{"
    char i = input.get();
    string numb;
    int fir;
    int sec;
    while (i != '}'){
        if(i == ':'){
            fir = stoi(numb);
            numb = "";

        }else if(i == ','){
            sec = stoi(numb);
            numb= "";
            freqTable[fir] = sec;
            input.get(); // get ' ' som vi inte vill ha med
        }else{
            numb.push_back(i);
        }
        i = input.get();

    }

    freqTable[PSEUDO_EOF] = 1;


    HuffmanNode* encodingTree = buildEncodingTree(freqTable);
    decodeData(input, encodingTree, output);
    freeTree(encodingTree);

}


/*
 * tar bort root noden med dess zero och one
*/
void freeTree(HuffmanNode* node){
    if (node == nullptr){
        return;
    }
    freeTree(node->zero);
    freeTree(node->one);
    delete node;
}
