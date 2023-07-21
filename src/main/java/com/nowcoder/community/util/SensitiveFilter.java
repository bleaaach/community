package com.nowcoder.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SensitiveFilter {
    //替换符
    public static final String REPLACEMENT="***";

    //根节点
    private TrieNode rootNode=new TrieNode();

    //在实例化bean后，服务器初始化之前调用
    @PostConstruct
    public void init(){
        try(
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword=reader.readLine())!=null){
                //将敏感词添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            log.error("加载敏感词文件失败："+e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树中
    private void addKeyword(String keyword){
        TrieNode tempNode=rootNode;
        for(int i=0;i<keyword.length();i++){
            char c=keyword.charAt(i);
            TrieNode subNode=tempNode.getSubNode(c);

            if(subNode==null){
                //初始化子节点
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //存在的话，指向子节点，进入下一轮循环
            tempNode=subNode;

            //设置结束标记
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }

        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return
     */
    public String filter(String text){
        if(text.isBlank()){
            return null;
        }

        //指针1
        TrieNode tempNode=rootNode;
        //指针2
        int begin=0;
        //指针3
        int position=0;
        //结果
        StringBuilder sb=new StringBuilder();

        while(begin<text.length()){
            if(position<text.length()){
                char c=text.charAt(position);

                //跳过符号
                if(isSymbol(c)){
                    //若指针1处于根节点，将此符号计入结果，让指针2走向下一步
                    if(tempNode == rootNode){
                        sb.append(c);
                        begin++;
                    }
                    //无论符号在开头或者中间，指针3都向下走一步
                    position++;
                    continue;
                }

                //检查下级节点
                tempNode=tempNode.getSubNode(c);
                if(tempNode==null){//没有下个节点，说明是最后节点，而且之前的词汇都不是敏感词
                    //以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    //进入下一个位置的过滤词的判断，这一波的过滤词已经结束了，开始新的一轮
                    position = ++begin;
                    //重新指向根节点
                    tempNode=rootNode;
                }else if(tempNode.isKeywordEnd()){
                    //发现了并完全匹配敏感词，将begin-position字符串替换掉
                    sb.append(REPLACEMENT);
                    //进入下一个位置的过滤词的判断，这一波的过滤词已经结束了，开始新的一轮，从这个过滤词的结束位置+1开始算
                    begin = ++position;
                    //重新指向根节点
                    tempNode=rootNode;
                }else{
                    //检查下一个字符
                    position++;
                }
            }else{
                //position遍历越界仍未匹配到敏感词，就将当前字符加入结果，开始下一个字符的过滤词筛查
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=rootNode;
            }
        }


        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF是东亚文字范围，isAsciiAlphanumeric判断是不是普通字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80||c>0x9FFF);
    }



    //前缀树
    private class TrieNode{
        //关键词标识
        private boolean isKeywordEnd = false;

        //子节点(key是下级字符，value是下级节点)
        private Map<Character,TrieNode> subNodes=new HashMap<>();

        private boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd=keywordEnd;
        }

        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }


}
