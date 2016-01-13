package controllers;

import com.twitter.Extractor;
import model.Comment;
import model.Meep;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by santiagomarti on 1/13/16.
 */
public class HashtagController {

    public Meep extractHashtags(Meep meep){
        Extractor ex = new Extractor();
        List<String> aux = ex.extractHashtags(meep.message);
        LinkedList<String> ret = new LinkedList<>();
        ret.addAll(aux);
        meep.hashtags = ret;
        return meep;
    }

    public Comment extractHashtags(Comment comment){
        Extractor ex = new Extractor();
        List<String> aux = ex.extractHashtags(comment.message);
        LinkedList<String> ret = new LinkedList<>();
        ret.addAll(aux);
        comment.hashtags = ret;
        return comment;
    }

    public LinkedList<String> extractHashtags(String arg){
        Extractor ex = new Extractor();
        List<String> aux = ex.extractHashtags(arg);
        LinkedList<String> ret = new LinkedList<>();
        ret.addAll(aux);
        return ret;
    }
}
