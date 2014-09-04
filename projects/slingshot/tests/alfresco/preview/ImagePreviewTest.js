/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon"], 
        function (registerSuite, expect, assert, require, TestCommon) {

   registerSuite({
      name: 'Previewer Tests',
      'Image Preview': function () {

         var browser = this.remote;
         var testname = "Image Preview Test";
         return TestCommon.loadTestWebScript(this.remote, "/ImagePreview", testname)

         .end()

         // A long sleep is required to wait for the binary data to be loaded.
         // However, it's worth noting that the binary data is *not* actually used because
         // the image preview creates an <img> element rather than making an XHR request.
         // However, the code has been left for education purposes for now. This test is therefore
         // not actually expected to render an image (due to a lack of authentication and a non-existant nodeRef)
         .sleep(5000)
         .findByCssSelector(".alfresco-preview-AlfDocumentPreview > div.previewer")
            .then(null, function() {
               assert(false, "Test #1a - Couldn't find preview node");
            })
            .end()

         .findAllByCssSelector(".alfresco-testing-MockXhr table tbody tr")
            .then(function(elements) {
               assert(elements.length === 1, "Test #2a - Expected just one XHR request, found: " + elements.length);
            })
            .end()

         .findByCssSelector(".alfresco-testing-MockXhr table tbody tr:first-child td.mx-url")
            .getVisibleText()
            .then(function(text) {
               var result = (text.indexOf("/share/service/components/documentlibrary/data/node/workspace/SpacesStore/62e6c83c-f239-4f85-b1e8-6ba0fd50fac4?view=browse&noCache") !== -1);
               assert(result, "Test #2b - AlfDocument didn't request node details: " + text);
            })
            .end()

         
         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      }
   });
});