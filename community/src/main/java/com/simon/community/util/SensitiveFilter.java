package com.simon.community.util;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 */
@Component
@Slf4j
public class SensitiveFilter {

    private TrieNode root = new TrieNode();

    //敏感词替换符
    private static final String REPLACEMENT="*";

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class TrieNode{
        //关键词结束表示
        private boolean end=false;
        //存放子节点,key为子节点的字符，value为对应节点
        private Map<Character,TrieNode> children=new HashMap<>();

        //添加子节点
        public void addChild(TrieNode node,Character ch){
            children.put(ch,node);
        }

        //获取子节点
        public TrieNode getChild(Character ch){
            return children.get(ch);
        }

        //是否包含某个子节点
        public boolean containsChild(Character ch){
            return children.containsKey(ch);
        }
    }

    /**
     * @purpose 根据敏感词来构建前缀树
     * */
    @PostConstruct
    public void init(){
        try (
                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //转成包装字符流
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ){
            String key=null;
            while((key=reader.readLine())!=null){
                //敏感词添加进树
                insert(key);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败："+e.getMessage());
        }

    }

    /**
     * @purpose 插入字符串到前缀树中
     * */
    private void insert(String word){
        TrieNode cur=root;
        for(int i=0;i<word.length();i++){
            char c=word.charAt(i);
            if(!cur.containsChild(c)){
                cur.addChild(new TrieNode(),c);
            }
            cur=cur.getChild(c);
        }
        cur.setEnd(true);
    }

    /**
     * @param text 待过滤的文本
     * @return 剔除该字符串中的敏感词后的文本
     * */
    public String filter(String text){
        if (StringUtils.isBlank(text))
            return null;
        StringBuilder sb = new StringBuilder();
        TrieNode cur=root;
        int slow=0;
        int fast=0;
        //用快指针过滤会更快
        while(fast<text.length()){
            char ch=text.charAt(fast);
            //跳过特殊符号，对于#s#b#这种也需要过滤
            if(isSpecial(ch)){
                //当cur在根节点时，代表开启一轮新的匹配
                // 此时slow=fast，都需要移动,并且将字符加入到sb中
                if(cur==root){
                    slow++;
                    sb.append(ch);
                }
                fast++;
                continue;
            }
            //检查树中是否存在该字符
            cur = cur.getChild(ch);
            if(cur==null){
                //以slow开始的字符串不是敏感词
                sb.append(ch);
                cur=root;
                slow++;
                fast=slow;
            }else if (cur.isEnd()){
                //检测到敏感词，每个字符都替换成*
                while(slow<=fast){
                    sb.append(REPLACEMENT);
                    slow++;
                }
                fast=slow;
                cur=root;
            }else{
                //有下级节点，检查下一个字符
                fast++;
            }
        }
        //末尾可能有剩余字符串没有加到sb中
        sb.append(text.substring(slow));
        return sb.toString();
    }

    /**
     * 判断是否为特殊符号
     * @return false 是字母或者是数字
     * @return true 是符号
     *
     * */
    private boolean isSpecial(Character ch){
        //0x2E80 到 0x9FFF 是 Unicode 中中日韩（CJK）汉字、假名、偏旁部首等符号的核心分布范围
        //认为汉字等也是正常字符
        return !CharUtils.isAsciiAlphanumeric(ch) && (ch<0x2E80 || ch>0x9FFF);
    }

}
