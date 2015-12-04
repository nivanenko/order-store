public class Test {
    public static void main(String[] args) {
        String data = "------WebKitFormBoundary3QC5vb1wTpSsB3ND\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"1.xml\"\n" +
                "Content-Type: text/xml\n" +
                "\n" +
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<order>\n" +
                "    <from zip=\"10001\" state=\"NY\" city=\"NEW YORK\"/>\n" +
                "    <to zip=\"20001\" state=\"DC\" city=\"WASHINGTON\"/>\n" +
                "    <lines>\t\n" +
                "        <line weight=\"1000.1\" volume=\"666\" hazard=\"true\" product=\"petrol\"/>\n" +
                "        <line weight=\"2000\" volume=\"666\" hazard=\"false\" product=\"water\"/>\n" +
                "    </lines>\n" +
                "    <instructions>here be dragons</instructions>\n" +
                "</order> \n" +
                "\n" +
                "------WebKitFormBoundary3QC5vb1wTpSsB3ND--";
        String s = data.substring(
                data.lastIndexOf("<order>"),
                data.lastIndexOf("</order>") + 8);


        System.out.println(s);
    }
}
