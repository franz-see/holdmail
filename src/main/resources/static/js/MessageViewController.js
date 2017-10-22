/*******************************************************************************
 * Copyright 2016 Sparta Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

(function () {
    'use strict';

    angular.module('HoldMailApp')

        .controller('MessageViewController', ['$scope', '$routeParams', '$location', 'MessageService',
            function ($scope, $routeParams, $location, MessageService) {

                var that = this;

                that.init = function() {

                    console.log('off we go = ' + $routeParams.messageId);

                    MessageService.getMessageDetail($routeParams.messageId)
                        .then(function (response) {
                            that.message = response.data;
                            that.messageHTMLURL = MessageService.getMessageContentURI(that.message.messageId, 'html');
                            that.messageTextURL = MessageService.getMessageContentURI(that.message.messageId, 'text');
                            that.messageRawURL = MessageService.getMessageContentURI(that.message.messageId, 'raw');

                        }, function () {
                            console.log('Service failed to query message details');
                        });
                };

                that.forwardMail = function () {

                    var recipient = $scope.forwardRecipient;

                    MessageService.forwardMessage(that.messageId, recipient)
                        .then(function () {
                            growl.success('Mail ' + that.messageId + ' successfully sent to <b>' + recipient + '</b>', {});

                        },function () {
                            growl.error("The server rejected the request (it probably didn't like that email address " +
                                "- see the logs for more info).", {});
                        });
                };


                that.init();
            }]);


}());
