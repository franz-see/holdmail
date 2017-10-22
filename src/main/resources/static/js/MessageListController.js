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

        .controller('MessageListController', ['$scope', '$location', 'MessageService',
            function ($scope, $location, MessageService) {

                var that = this;

                that.items = [];
                that.busy = false;
                that.noMorePages = false;
                that.page = 0;
                that.size = 40;

                that.clearAndFetchMessages = function () {
                    that.items = [];
                    that.page = 0;
                    that.noMorePages = false;
                    that.fetchMessages();
                };

                that.showEmptyMessagesPane = function() {

                    return !this.busy && that.items.length < 1;
                };

                that.fetchMessages = function () {

                    if (this.busy || this.noMorePages) {
                        return;
                    }

                    // service is async; tell controller/infinite scroll to back off until response comes through
                    this.busy = true;

                    MessageService.getMessageList(that.size, that.page, $scope.recipientEmail)
                        .then(function (response) {

                            var messages = response.data.messages;

                            that.items = that.items.concat(messages);
                            that.noMorePages = messages.length < 1;
                            that.busy = false;
                            that.page = that.page + 1;

                        }, function () {
                            console.log('Service failed to query message list');

                            // keep busy set otherwise infinite scroll will go crazy making followup calls
                            that.busy = true;
                        });

                };


                that.rowClick = function (selectedMail) {
                    $location.url('/view/' + selectedMail.messageId);
                };


                that.clearAndFetchMessages();

            }]);


}());
