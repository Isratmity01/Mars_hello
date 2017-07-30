/*
    =============================================================================
    *****************************************************************************
    The contents of this file are subject to the Mozilla Public License
    Version 1.1 (the "License"); you may not use this file except in
    compliance with the License. You may obtain a copy of the License at
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS"
    basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
    License for the specific language governing rights and limitations
    under the License.

    The Original Code is JAvroPhonetic

    The Initial Developer of the Original Code is
    Rifat Nabi <to.rifat@gmail.com>

    Copyright (C) OmicronLab (http://www.omicronlab.com). All Rights Reserved.


    Contributor(s): ______________________________________.

    *****************************************************************************
    =============================================================================
*/

package com.omicronlab.avro;

import android.content.Context;
import android.content.res.XmlResourceParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.grameenphone.mars.R;
import com.omicronlab.avro.phonetic.*;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class PhoneticXmlLoader implements PhoneticLoader {

    private URL url = null;
    private static final String ns = null;
    private XmlResourceParser parser;
    private static Data data = new Data();

    public PhoneticXmlLoader(Context context) {

        parser = context.getResources().getXml(R.xml.phonetic);

        try {
            parser.next();
            parser.next();
            parser.require(XmlPullParser.START_TAG, ns, "data");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the classes tag


                switch (name) {
                    case "classes":
                        readClasses(parser);
                        break;
                    case "patterns":
                        readPatterns(parser);
                        break;
                    default:
                        skip(parser);
                        break;
                }


            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public PhoneticXmlLoader(String path) throws MalformedURLException {
        this.url = new URL(path);
    }


    public Data getData() {

        return data;
    }











    private void readClasses(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "classes");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("vowel")) {
                data.setVowel( readTag(parser, "vowel") );
            } else if (name.equals("consonant")) {
                data.setConsonant( readTag(parser, "consonant") );
            } else if (name.equals("casesensitive")) {
                data.setCasesensitive( readTag(parser, "casesensitive") );
            } else {
                skip(parser);
            }
        }
    }







    private void readPatterns(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "patterns");



        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case "pattern":
                    readPattern(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }


        }

    }




    private void readPattern(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "pattern");

        Pattern p = new Pattern();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("find")) {
                p.setFind( readTag(parser, "find") );
            } else if (name.equals("replace")) {
                p.setReplace( readTag(parser, "replace") );
            } else if (name.equals("rules")) {
                ArrayList<Rule> ru = readRules(parser);
                p.addRules( ru );
            } else {
                skip(parser);
            }


        }
        data.addPattern(p);
    }




    private ArrayList<Rule> readRules(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "rules");

        ArrayList<Rule> rules = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("rule")) {
                Rule r = readRule(parser);
                rules.add(r);
            } else {
                skip(parser);
            }


        }
        return rules;
    }


    private Rule readRule(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "rule");

        Rule r = new Rule();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("find")) {
                ArrayList<Match> matches = readMatches(parser);
                r.addMatches(matches);
            } else if (name.equals("replace")) {
                r.setReplace( readTag(parser, "replace") );
            }else {
                skip(parser);
            }


        }
        return r;
    }







    private ArrayList<Match> readMatches(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "find");
        parser.next();

        ArrayList<Match> matches = new ArrayList<>();
        String name = parser.getName();

        while (name.equals("match")) {
            Match m = new Match();
            String type = parser.getAttributeValue(null, "type");
            String scope = parser.getAttributeValue(null, "scope");
            String value = readTag(parser,"match");

            m.setType(type);
            m.setScope(scope);
            m.setValue(value);

            matches.add(m);

            parser.next();
            name = parser.getName();

        }


        return matches;
    }

























    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return title;
    }


    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }




    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }







}
