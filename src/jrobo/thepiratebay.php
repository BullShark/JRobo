<?php

$debug = false;
$limit = 3;
if (isset($_GET['name']) && isset($_GET['orderby'])) {

    if (isset($_GET['limit'])) {
        $limit = $_GET['limit'];
    }

    if ($debug) {
        header("Content-Type: text/plain; charset=UTF-8");
    }
    // http://thepiratebay.org/search/test/5/7/0
    // http://thepiratebay.org/search/softwear name/single line/order by/0
    // single line =5
    //order by seeder = 7
    // order by peer = 9
    // order by date =3 
    // order by name =2

    define("FS_USER_AGENT", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.3) Gecko/20070309 Firefox/2.0.0.3");
    $url = "http://thepiratebay.org/search/" . urlencode(trim($_GET['name'])) . "/0/" . getOrderby(trim($_GET['orderby'])) . "/0";
    if ($debug) {
        echo $url . "\n";
    }
    $html = executeURL($url);
    $data = HTMLParser($html);
    if ($debug) {
        print_r($data);
    } else {
        if (isset($_GET['type'])) {
            if ($_GET['type'] == "xml") {
                xmlout($data);
            } else {
                jsonout($data);
            }
        } else {
            jsonout($data);
        }
    }
} else {
    help();
}

function getOrderby($orderby) {
//    echo "    Seeders = 7 or s   Peer=9 or p \n";
//    echo "    Date = 3 or d      Name = 2 or n \n";  

    switch ($orderby) {
        case "7":
        case "s":
            $out = "7";
            break;
        case "3":
        case "d":
            $out = "3";
            break;
        case "9":
        case "l":
            $out = "9";
            break;
        case "2":
        case "n":
            $out = "2";
            break;
        default:
            $out = "0";
    }
    return $out;
}

function HTMLParser($html) {
    global $debug, $limit;
    // .//*[@class='t1']
    $dom = new DOMDocument();
    @$dom->loadHTML($html);
    $xpath = new DOMXPath($dom);
    unset($html, $dom);
    $out = array();
    $row = 0;

    $location = ".//*[@id='searchResult']/tr";
    $rootnode = $xpath->query($location);
    if (!is_null($rootnode)) {
        foreach ($rootnode as $nodes) {
            if ($debug)
                echo "1->" . $row . " " . $nodes->nodeName . "\n";
            if ($nodes->haschildNodes()) {
                $column = 0;
                $secondtype = false;
                foreach ($nodes->childNodes as $node1) {
                    if ($debug)
                        echo "\t2->" . $column . " " . $node1->nodeName . "\n";
                    if ($node1->haschildNodes()) {
                        foreach ($node1->childNodes as $node2) {
                            if ($debug)
                                echo "\t\t 3 " . $node2->nodeName . "\n";
                            if ($node2->haschildNodes()) {
                                foreach ($node2->childNodes as $node3) {
                                    if ($debug)
                                        echo "\t\t\t 4. " . $node3->nodeName . "\t" . $node3->nodeValue . "\n";
                                    /// get result type 
                                    if ($node3->nodeName == "a" && $column == 0 && $secondtype == true) {
                                        $out[$row]["type"] = $out[$row]["type"] . "/" . trim($node3->nodeValue);
                                    }
                                    if ($node3->nodeName == "a" && $column == 0 && $secondtype == false) {
                                        $out[$row]["type"] = trim($node3->nodeValue);
                                        $secondtype = true;
                                    }
                                    // get result name and url
                                    if ($node2->nodeName == "div" && $node3->nodeName == "a" && $column == 2) {
                                        $out[$row]["name"] = (trim($node3->nodeValue));
                                        $out[$row]["url"] = "http://thepiratebay.org" . trim($node3->getAttribute('href'));
                                        $out[$row]["tinyurl"] = createTinyUrl("http://thepiratebay.org" . trim($node3->getAttribute('href')));
                                    }
                                    // get result details
                                    if ($node2->nodeName == "font" && $column == 2) {
                                        $details = details((trim($node2->nodeValue)));
                                        $out[$row]["Uploaded"] = $details[0];
                                        $out[$row]["Size"] = $details[1];
                                        $out[$row]["ULed"] = $details[2];
                                    }
                                }
                            }
                        }
                    }
                    // get result leechers && seeders
                    if ($node1->nodeName == "td" && $column == 4) {
                        $out[$row]["seeders"] = trim($node1->nodeValue);
                    }
                    if ($node1->nodeName == "td" && $column == 6) {
                        $out[$row]["leechers"] = trim($node1->nodeValue);
                    }

                    $column++;
                }
            }
            $row++;
            if ($row >= $limit) {
                break;
            }
        }
    }
    return $out;
}

function createTinyUrl($strURL) {
    $tinyurl = file_get_contents("http://tinyurl.com/api-create.php?url=" . $strURL);
    return $tinyurl;
}

function details($string) {
    $pieces = explode(",", $string);
    $pieces[0] = trim(str_replace("Uploaded", "", $pieces[0]));
    $pieces[1] = trim(str_replace("Size", "", $pieces[1]));
    $pieces[2] = trim(str_replace("ULed by", "", $pieces[2]));
    return $pieces;
}

function jsonout($data) {
    header('content-type: application/json; charset=utf-8');
    echo json_encode($data);
}

function xmlout($data) {
    global $url;
    $xmlcompleate = "";
    $row = 0;
    foreach ($data as $row) {
        $xmlcompleate .="<item>";
        $xmlcompleate .="<type><![CDATA[" . $row["type"] . "]]></type>";
        $xmlcompleate .="<name><![CDATA[" . $row["name"] . "]]></name>";
        $xmlcompleate .="<url><![CDATA[" . $row["url"] . "]]></url>";
        $xmlcompleate .="<tinyurl><![CDATA[" . $row["tinyurl"] . "]]></tinyurl>";
        $xmlcompleate .="<uploaded>" . $row["Uploaded"] . "</uploaded>";
        $xmlcompleate .="<size>" . $row["Size"] . "</size>";
        $xmlcompleate .="<uled><![CDATA[" . $row["ULed"] . "]]></uled>";
        $xmlcompleate .="<seeders>" . $row["seeders"] . "</seeders>";
        $xmlcompleate .="<leechers>" . $row["leechers"] . "</leechers>";
        $xmlcompleate .="</item>";
        $row++;
    }
    header('Content-Type: text/xml; charset=UTF-8');
    $response = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
    $response .= "<response>";
    $response .="<url><![CDATA[" . $url . "]]></url>";
    $response .= $xmlcompleate;
    $response .= "</response>";
    echo $response;
}

function executeURL($url, $postString = NULL, $header = NULL, $referer = NULL) {
    $ch = curl_init();

// header to be used in HTTP request otherwise server reply empty response
    curl_setopt($ch, CURLOPT_USERAGENT, FS_USER_AGENT);
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_FAILONERROR, true);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_setopt($ch, CURLOPT_AUTOREFERER, true);
    if ($referer)
        curl_setopt($ch, CURLOPT_REFERER, $referer);
// whether to return transfer or keep on specified url
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
// whether to include header portion of page
    curl_setopt($ch, CURLOPT_HEADER, false);
// 0 for wait indefinite;
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 0);
    if ($header) {
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_ENCODING, 'gzip,deflate');
    }
    if ($postString)
        curl_setopt($ch, CURLOPT_POSTFIELDS, $postString);

    $html = curl_exec($ch);
    if (!$html) {
// Error
        echo $url . "<br>";
        echo "<br />cURL error number:" . curl_errno($ch);
        echo "<br />cURL error:" . curl_error($ch);
// XXXX: Return error only in case of yelp (to change proxy in case of yelp and stop paring of other sites)
        $html = (strpos($url, "http://www.canbrowse.com/index.php?____pgfa=aHR0cDovL3d3dy55ZWxwLmNvbS9zZWFyY2g%253D") !== false || strpos($url, "yelp.com") !== false) ? curl_error($ch) : $html;
//exit;
    }
    curl_close($ch);
    return $html;
}

function left($str, $length) {
    return substr($str, 0, $length);
}

function right($str, $length) {
    return substr($str, -$length);
}

function help() {
    header("Content-Type: text/plain; charset=UTF-8");
    echo "JRobo - thepiratebay \n";
    echo "thepiratebay.php?name=matrix&orderby=s&type=json&limit=3 \n\n";
    echo "API PARAMETER DETAILS:\n";
    echo "orderby :- \n";
    echo "    Seeders = 7 or s   Leechers=9 or l \n";
    echo "    Date = 3 or d      Name = 2 or n \n";
    echo "type:- \n";
    echo "    xml or json\n\n";
    echo "NOTE: name and orderby is required\n";
    echo "      default type json\n";
    echo "      default limit 3 result\n";
}

?>
