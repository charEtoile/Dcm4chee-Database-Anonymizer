package com.liberado.util;

import org.dcm4che.dict.DictionaryFactory;
import org.dcm4che.dict.TagDictionary;
import org.dcm4che.dict.Tags;

/**
 * Created by michelliberado on 09/07/2017.
 */
public class DcmElementOccurenceCount {
    private int count = -1;
    private int tag = -1;
    private String name;

    public int getCount() {
        return this.count;
    }

    public void setCount(int newCount) {
        if (this.tag == -1) {
            // There is no point to modify the count
            return;
        }

        if (newCount < -1) {
            // No need to go further than -1, a negative value means that it is unused
            newCount = -1;
            return;
        }

        // Now we can set the requested value
        this.count = newCount;
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int newTag) throws Exception {
        TagDictionary td = DictionaryFactory.getInstance().getDefaultTagDictionary();
        TagDictionary.Entry e = td.lookup(newTag);
        if (e != null) {
            if (newTag != this.tag) {
                this.tag = newTag;
                count = 0;
                this.name = e.name;
            }
            else {
                // Nothing to do, the tag is the same
            }
        }
        else {
            throw new Exception("Invalid tag " + newTag);
        }
    }


    public DcmElementOccurenceCount(int tag) throws Exception {
        setTag(tag);
    }

    @Override
    public String toString() {
        if (tag == -1) {
            return "Undefined tag";
        }
        return Tags.toString(this.tag) + " - " + name + " - count: " + this.count;
    }
}
