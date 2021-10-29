/*
 * Copyright (c) 2002-2019, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.maven.xdoc2md;

import java.text.MessageFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XDoc2MarkdownHandler
 */
public class XDoc2MarkdownHandler extends DefaultHandler
{
    private static final String[][] SONAR_BADGES = {
        { "Alerte" , "alert_status" },
        { "Line of code" , "ncloc" },
        { "Coverage" , "coverage" },
    };
    
    private static final String TAG_BODY = "body";
    private static final String TAG_SECTION = "section";
    private static final String TAG_SUBSECTION = "subsection";
    private static final String TAG_STRONG = "strong";
    private static final String TAG_BOLD = "b";
    private static final String TAG_EM = "em";
    private static final String TAG_CODE = "code";
    private static final String TAG_PARAGRAPH = "p";
    private static final String TAG_PRE = "pre";
    private static final String TAG_LI = "li";
    private static final String TAG_UL = "ul";
    private static final String TAG_TR = "tr";
    private static final String TAG_TH = "th";
    private static final String TAG_TD = "td";
    private static final String TAG_TABLE = "table";
    private static final String TAG_ANCHOR = "a";
    private static final String TAG_IMAGE = "img";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_HREF = "href";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ATTRIBUTE_ALT = "alt";
    private static final String ATTRIBUTE_LANGUAGE = "language";
    private static final String URL_XDOC = "https://dev.lutece.paris.fr/plugins/";
    private static final String URL_SONAR_SERVER = "https://dev.lutece.paris.fr/sonar";
    private static final String BADGE_FORMAT = "\n[![{0}]({1}/api/project_badges/measure?project=fr.paris.lutece.plugins%3A{3}&metric={2})]" +
                           "({1}/dashboard?id=fr.paris.lutece.plugins%3A{3})";
    private static final String BADGE_FORMAT_CORE = "\n[![{0}]({1}/api/project_badges/measure?project=fr.paris.lutece%3A{3}&metric={2})]" +
                           "({1}/dashboard?id=fr.paris.lutece%3A{3})";
    private static final String HEADER = "![]({0})";
    private static final String FOOTER =
        "\n\n *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*";
    private static final String BUILD_STATUS_URL = "https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=";
    private static final String ARTIFACT_CORE = "lutece-core";
    private StringBuilder _sbDocument;
    private boolean _bBody;
    private boolean _bPRE;
    private int _nTableRowCount;
    private int _nTableColumnCount;
    private String _strLink;
    private boolean _bAnchor;
    private String _strArtifactId;

    /**
     * Constructor
     * @param strArtifactId The artifact id
     * @param strRepository
     */
    public XDoc2MarkdownHandler( String strArtifactId, String strRepository )
    {
        _strArtifactId = strArtifactId;
        String strUrl = BUILD_STATUS_URL + getJobName( strRepository );
        Object[] arguments = { strUrl };
        String strHeader = MessageFormat.format( HEADER, arguments );
        _sbDocument = new StringBuilder( strHeader );
        
        addSonarBadges( _sbDocument , strArtifactId );
    }

    /**
     * Returns The MD Document
     *
     * @return The MD Document
     */
    public String getDocument(  )
    {
        return _sbDocument.toString(  );
    }

    /**
     * {@inheritDoc }
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startElement( String uri, String localName, String qName, Attributes attributes )
                      throws SAXException
    {
        if ( qName.equalsIgnoreCase( TAG_BODY ) )
        {
            _bBody = true;
        } 
        else if ( qName.equalsIgnoreCase( TAG_SECTION ) )
        {
            _sbDocument.append( "\n# " ).append( attributes.getValue( ATTRIBUTE_NAME ) ).append( "\n" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_SUBSECTION ) )
        {
            _sbDocument.append( "\n## " ).append( attributes.getValue( ATTRIBUTE_NAME ) ).append( "\n" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_STRONG ) || qName.equalsIgnoreCase( TAG_BOLD ) )
        {
            _sbDocument.append( " **" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_EM ) )
        {
            _sbDocument.append( " *" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_CODE ) )
        {
            _sbDocument.append( " `" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_PARAGRAPH ) )
        {
            _sbDocument.append( "\n" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_PRE ) )
        {
            _sbDocument.append( "\n```" );
            if ( attributes.getValue( ATTRIBUTE_LANGUAGE ) != null )
            {
                _sbDocument.append( attributes.getValue( ATTRIBUTE_LANGUAGE ) );
            }
            _sbDocument.append( "\n" );
            _bPRE = true;
        } 
        else if ( qName.equalsIgnoreCase( TAG_LI ) )
        {
            _sbDocument.append( "\n* " );
        } 
        else if ( qName.equalsIgnoreCase( TAG_UL ) )
        {
            _sbDocument.append( "\n " );
        } 
        else if ( qName.equalsIgnoreCase( TAG_TABLE ) )
        {
            _sbDocument.append( "\n" );
            _nTableRowCount = 0;
            _nTableColumnCount = 0;
        } 
        else if ( qName.equalsIgnoreCase( TAG_TR ) )
        {
            _nTableRowCount++;
        } 
        else if ( qName.equalsIgnoreCase( TAG_TH ) )
        {
            _sbDocument.append( "| " );
            _nTableColumnCount++;
        } 
        else if ( qName.equalsIgnoreCase( TAG_TD ) )
        {
            _sbDocument.append( "| " );
            _nTableColumnCount++;
        } 
        else if ( qName.equalsIgnoreCase( TAG_ANCHOR ) )
        {
            _bAnchor = true;
            _strLink = attributes.getValue( ATTRIBUTE_HREF );
        } 
        else if ( qName.equalsIgnoreCase( TAG_IMAGE ) )
        {
            _sbDocument.append( getImage( attributes.getValue( ATTRIBUTE_SRC ),
                                          attributes.getValue( ATTRIBUTE_ALT ) ) );
        }
    }

    /**
     * {@inheritDoc }
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endElement( String uri, String localName, String qName )
                    throws SAXException
    {
        if ( qName.equalsIgnoreCase( TAG_STRONG ) || qName.equalsIgnoreCase( TAG_BOLD ) )
        {
            _sbDocument.append( "** " );
        } 
        else if ( qName.equalsIgnoreCase( TAG_EM ) )
        {
            _sbDocument.append( "* " );
        } 
        else if ( qName.equalsIgnoreCase( TAG_CODE ) )
        {
            _sbDocument.append( "` " );
        } 
        else if ( qName.equalsIgnoreCase( TAG_PARAGRAPH ) )
        {
            _sbDocument.append( "\n" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_PRE ) )
        {
            _sbDocument.append( "\n```\n" );
            _bPRE = false;
        } 
        else if ( qName.equalsIgnoreCase( TAG_UL ) )
        {
            _sbDocument.append( "\n" );
        } 
        else if ( qName.equalsIgnoreCase( TAG_TR ) )
        {
            _sbDocument.append( "|\n" );

            if ( _nTableRowCount == 1 )
            {
                for ( int i = 0; i < _nTableColumnCount; i++ )
                {
                    _sbDocument.append( "|-----------------" );
                }

                _sbDocument.append( "|\n" );
            }
        } 
        else if ( qName.equalsIgnoreCase( TAG_ANCHOR ) )
        {
            _bAnchor = false;
        } 
        else if ( qName.equalsIgnoreCase( TAG_BODY ) )
        {
            _sbDocument.append("\n\n[Maven documentation and reports](" + URL_XDOC).append(_strArtifactId).append("/)\n\n");
            _sbDocument.append( FOOTER );
        }
    }

    /**
     * {@inheritDoc }
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void characters( char[] ch, int start, int length )
                    throws SAXException
    {
        if ( _bAnchor )
        {
            _sbDocument.append( " [" ).append( new String( ch, start, length ).trim(  ) ).append( "](" )
                       .append( _strLink.trim(  ) ).append( ") " );
        } else if ( _bBody )
        {
            String strText = new String( ch, start, length );

            if ( ! _bPRE )
            {
                strText = strText.trim(  ).replaceAll( "\\s+", " " );
            }

            _sbDocument.append( strText );
        }
    }

    /**
     * Build the image code
     * @param strSource The image source url
     * @param strAlt The alternate text
     * @return The image code
     */
    private String getImage( String strSource, String strAlt )
    {
        String strUrl = strSource;
        String strText = ( strAlt != null ) ? strAlt : "";

        if ( ! strSource.startsWith( "http" ) )
        {
            strUrl = URL_XDOC + _strArtifactId + "/" + strSource;
        }

        return "![" + strText + "](" + strUrl + ")";
    }

    /**
     * Gets the build job name form the repository name
     * @param strRepository the repository name
     * @return the build job name 
     */
    private String getJobName( String strRepository )
    {
        String strJobName = strRepository.replaceAll( "lutece-", "" );
        strJobName = strJobName.replaceAll( ".git" , "" );
        strJobName += "-deploy";
        return strJobName;
    }

    /**
     * Add SonarQube badges
     * @param sbMarkDown The Markdown String Buffer
     * @param strArtifactId The artifact ID
     */
    private void addSonarBadges( StringBuilder sbMarkDown, String strArtifactId )
    {
        for( String[] badge : SONAR_BADGES )
        {
            sbMarkDown.append( buildBadge( badge[0] , badge[1] , strArtifactId ));
        }
        sbMarkDown.append( "\n" );
    }
    
    /**
     * Build a SonarQube badge
     * @param strMetricName The name of the metric
     * @param strMetricId The id of the metric
     * @param strArtifactId The artifact ID
     * @return The badge using Markdown syntax
     */
    private String buildBadge( String strMetricName, String strMetricId , String strArtifactId )
    {
        Object[] args = { strMetricName, URL_SONAR_SERVER, strMetricId, strArtifactId };
        String strBadgeFormat = ( ARTIFACT_CORE.equals( strArtifactId )) ? BADGE_FORMAT_CORE : BADGE_FORMAT;
        return MessageFormat.format( strBadgeFormat , args );
    }
}
